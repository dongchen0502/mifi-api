package com.hxtx.service;

import com.hxtx.entity.*;
import com.hxtx.exception.ApiException;
import com.hxtx.listener.CacheCenter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * 实现api服务的具体业务逻辑
 * Created by dongchen on 16/3/27.
 */
@Service
public class ApiService {

    @Autowired
    ExchangeService exchange;

    private final String SuccCode = "0000";

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    private final int TYPE_BALANCE = 1;
    private final int TYPE_FLOWSET = 2;
    private final int TYPE_PAYMENT = 3;

    private String queryFromCache(int type, String mobile, String month){
        if(!CacheCenter.resultMap.containsKey(mobile)){
            return "";
        }

        CacheResult cacheResult = CacheCenter.resultMap.get(mobile);
        switch (type){
            case TYPE_BALANCE : {
                return cacheResult.getBalance();
            }
            case TYPE_FLOWSET : {
                return cacheResult.getFlowsetByMonth(month);
            }
            case TYPE_PAYMENT : {
                return cacheResult.getPaymentByMonth(month);
            }
            default: return "";
        }
    }
    private void updateCache(int type, String mobile, String month, String result){
        if(!CacheCenter.resultMap.containsKey(mobile)){
            CacheCenter.resultMap.put(mobile, new CacheResult());
        }

        CacheResult cacheResult = CacheCenter.resultMap.get(mobile);

        switch (type){
            case TYPE_BALANCE : {
                cacheResult.setBalance(result);
                break;
            }
            case TYPE_FLOWSET : {
                cacheResult.setFlowset(month, result);
                break;
            }
            case TYPE_PAYMENT : {
                cacheResult.setPayment(month, result);
                break;
            }
        }
        //单类缓存信息更新不触发缓存时间更新
//        timeMap.put(mobile, System.currentMillions());
    }
    /**
     * 查询手机号余额, 网关接口有限速,10秒钟超过10次查询则会异常
     *
     * @param mobile    查询的手机号码
     * @param queryType 查询类型
     * @return Balance
     */
    public Balance queryBalance(String mobile, int queryType, boolean useCache) {
        Balance result = null;

        String xml = "";
        if(useCache){
            xml = this.queryFromCache(TYPE_BALANCE, mobile, null);
        }
        if(StringUtils.isEmpty(xml)){
            xml = exchange.balance(mobile, queryType);
        }

        if (StringUtils.isEmpty(xml)) {
            return result;
        }else{
            this.updateCache(TYPE_BALANCE, mobile, null, xml);
        }

        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element TcpCont = root.element("TcpCont");
            String respCode = TcpCont.element("Response").element("RspCode").getText();

            if (SuccCode.equals(respCode)) {
                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");
                String ba = qryInfoRsp.element("BalanceAmount").getText();
                String shouldCharge = qryInfoRsp.element("ShouldCharge").getText();
                String sum = qryInfoRsp.element("SumCharge").getText();

                result = new Balance(ba, shouldCharge, sum);
            } else {
                String rspDesc = TcpCont.element("Response").element("RspDesc").getText();
                throw new ApiException(respCode + ":" + rspDesc);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询手机流量套餐, 网关接口有限速,一秒钟超过10次查询则会异常
     *
     * @param mobile 手机号
     * @param month  查询月份
     * @return FlowSet or null on unexpected exception
     */
    public List<FlowSet> queryFlowSet(String mobile, String month, boolean useCache) {
        List<FlowSet> result = new ArrayList<FlowSet>();

        month = month.replace("-", "");

        String xml = "";
        if(useCache){
            xml = this.queryFromCache(TYPE_FLOWSET, mobile, month);
        }
        if(StringUtils.isEmpty(xml)){
            xml = exchange.flowSet(mobile, month);
        }
        System.out.println(mobile + " | " + month + " queryFlowSet resp: \n" + xml);

        if (StringUtils.isEmpty(xml)) {
            return result;
        }else{
            this.updateCache(TYPE_FLOWSET, mobile, month, xml);
        }

        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element TcpCont = root.element("TcpCont");
            String respCode = TcpCont.element("Response").element("RspCode").getText();

            if (SuccCode.equals(respCode)) {
                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");

                Element typeId = qryInfoRsp.element("InfoTypeID");
                if ("13".equals(typeId.getText().trim())) {
                    Iterator ppIt = qryInfoRsp.elementIterator("PPAccuInfo");
                    while (ppIt.hasNext()) {
                        Element ppAccuEl = (Element) ppIt.next();
                        String prodOffName = ppAccuEl.element("ProdOffName").getText().trim();
                        String pStartTime = ppAccuEl.element("StartDate").getText().trim();
                        String pEndTime = ppAccuEl.element("EndDate").getText().trim();

                        FlowSet fs = new FlowSet();
                        List<SubAccuInfo> subAccuInfoList = new ArrayList<SubAccuInfo>();

                        fs.setProdOffName(prodOffName);
                        fs.setpStartTime(pStartTime);
                        fs.setpEndTime(pEndTime);
                        fs.setSubAccuInfoList(subAccuInfoList);

                        Iterator subIt = ppAccuEl.elementIterator("SubAccuInfo");
                        while (subIt.hasNext()) {
                            Element subAccuInfo = (Element) subIt.next();
                            String accuName = subAccuInfo.element("AccuName").getText().trim();
                            String startTime = subAccuInfo.element("StartTime").getText().trim();
                            String endTime = subAccuInfo.element("EndTime").getText().trim();
                            String accuAmount = subAccuInfo.element("AccuAmount").getText().trim();
                            String usedAmount = subAccuInfo.element("UsedAmount").getText().trim();
                            String transferAccuAmount = subAccuInfo.element("TransferAccuAmount").getText().trim();
                            String transferUsedAmount = subAccuInfo.element("TransferUsedAmount").getText().trim();
                            String unitTypeId = subAccuInfo.element("UnitTypeId").getText().trim();

                            SubAccuInfo sub = new SubAccuInfo();
                            sub.setAccuName(accuName);
                            sub.setStartTime(startTime);
                            sub.setEndTime(endTime);
                            sub.setAccuAmount(accuAmount);
                            sub.setUsedAmount(usedAmount);
                            sub.setTransferAccuAmount(transferAccuAmount);
                            sub.setTransferUsedAmount(transferUsedAmount);
                            sub.setUnitTypeId(unitTypeId);

                            subAccuInfoList.add(sub);
                        }

                        result.add(fs);
                    }
                }
            } else {
                String rspDesc = TcpCont.element("Response").element("RspDesc").getText();
                throw new ApiException(respCode, rspDesc);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询手机充值记录, 网关接口有限速,一秒钟超过10次查询则会异常
     *
     * @param mobile 手机号
     * @param month 查询月份
     * @return PaymentRecordInfo
     */
    public List<PaymentRecordInfo> queryChargeInfo(String mobile, String month, boolean useCache) {
        List<PaymentRecordInfo> result = new ArrayList<PaymentRecordInfo>();

        month = month.replace("-", "");

        String xml = "";
        if(useCache){
            xml = this.queryFromCache(TYPE_PAYMENT, mobile, month);
        }
        if(StringUtils.isEmpty(xml)){
            xml = exchange.chargeInfo(mobile, month);
        }
        System.out.println(mobile + " | " + month + " queryChargeInfo resp: \n" + xml);

        if (StringUtils.isEmpty(xml)) {
            return result;
        }else{
            this.updateCache(TYPE_PAYMENT, mobile, month, xml);
        }

        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element TcpCont = root.element("TcpCont");
            String respCode = TcpCont.element("Response").element("RspCode").getText();

            if (SuccCode.equals(respCode)) {
                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");

                Element typeId = qryInfoRsp.element("InfoTypeID");

                if ("16".equals(typeId.getText().trim())) {

                    Iterator it = qryInfoRsp.elementIterator("PaymentRecordInfo");
                    while (it.hasNext()) {
                        Element el = (Element) it.next();
                        String paymentAmount = el.element("PaymentAmount").getText().trim();
                        String paymentMethod = el.element("PaymentMethod").getText().trim();
                        String payTime = el.element("PayTime").getText().trim();

                        PaymentRecordInfo recordInfo = new PaymentRecordInfo();
                        recordInfo.setPaymentAmount(paymentAmount);
                        recordInfo.setPaymentMethod(paymentMethod);
                        recordInfo.setPayTime(payTime);

                        result.add(recordInfo);
                    }
                }
            } else {
                String rspDesc = TcpCont.element("Response").element("RspDesc").getText();
                throw new ApiException(respCode, rspDesc);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     *批量查询手机流量套餐
     *
     * @param mobiles
     * @param month
     * @return
     */
    public Map<String, FlowSetProfile> batchQueryFlowSet(String mobiles, String month) {
        if(mobiles == null){
            throw new ApiException("批量查询错误 : 参数 mobiles = null");
        }
        if(month == null){
            throw new ApiException("批量查询错误 : 参数 month = null");
        }

        String[] mobileArr = mobiles.split(",");
        month = month.replace("-", "");
        Map<String, FlowSetProfile> finalResult = new HashMap<String, FlowSetProfile>(mobileArr.length);

        CompletionService<FlowSetProfile> completionService = new ExecutorCompletionService<FlowSetProfile>(fixedThreadPool);
        for(String mobile : mobileArr){
            completionService.submit(queryFlowTask(mobile, month));
        }

        try {

            for (int i = 0; i < mobileArr.length; i++) {
                Future<FlowSetProfile> f =  completionService.take();

                FlowSetProfile fsp = f.get();

                finalResult.put(fsp.getMobile(), fsp);
            }

        }catch (InterruptedException e) {

            e.printStackTrace();
            throw new ApiException("服务器异常...", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new ApiException("服务器异常...");
        }

        return finalResult;
    }

    private Callable<FlowSetProfile> queryFlowTask(final String mobile, final String month){

        Callable task = new Callable() {

            public Object call() throws Exception {

                int tryTimes = 0;
                List<FlowSet> flowSet = new ArrayList<FlowSet>();

                while(tryTimes < 3){

                    try{
                        tryTimes++;
                        flowSet = queryFlowSet(mobile, month, true);
                        break;
                    }catch(ApiException e){
                        if("1002".equals(e.getErrCode())){
                            System.out.println(e.getMessage());
                            Thread.sleep(1000 * 10);
                        }else{
                            throw e;
                        }
                    }
                }

                FlowSetProfile result = new FlowSetProfile();

                Double totalF = 0d;
                Double monthF = 0d;
                if(!flowSet.isEmpty()){
                    for(SubAccuInfo each : flowSet.get(0).getSubAccuInfoList()){
                        double f = Double.parseDouble(each.getAccuAmount());
                        totalF += f;
                        String subMonth = each.getStartTime().substring(0, 6);
                        if(month.equals(subMonth)){
                            monthF = f;
                        }
                    }
                }

                result.setMobile(mobile);
                result.setMonth(month);
                result.setMonthTrafficLeft(monthF);
                result.setTotalTrafficLeft(totalF);

                return result;
            }
        };
        return task;
    }

    public Map<String, List<PaymentRecordInfo>> batchQueryChangeInfo(String mobiles, String month) {
        if(mobiles == null){
            throw new ApiException("批量查询错误 : 参数 mobiles = null");
        }
        if(month == null){
            throw new ApiException("批量查询错误 : 参数 month = null");
        }

        String[] mobileArr = mobiles.split(",");
        month = month.replace("-", "");
        Map<String, List<PaymentRecordInfo>> finalResult = new HashMap<String, List<PaymentRecordInfo>>(mobileArr.length);

        CompletionService<Map<String, List<PaymentRecordInfo>>> completionService = new ExecutorCompletionService<Map<String, List<PaymentRecordInfo>>>(fixedThreadPool);
        for(String mobile : mobileArr){
            completionService.submit(queryPaymentTask(mobile, month));
        }

        try {
            for (int i = 0; i < mobileArr.length; i++) {
                Future<Map<String, List<PaymentRecordInfo>>> f =  completionService.take();

                Map<String, List<PaymentRecordInfo>> chargeInfo = f.get();

                finalResult.putAll(chargeInfo);
            }

        }catch (InterruptedException e) {
            e.printStackTrace();
            throw new ApiException("服务器异常...", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new ApiException("服务器异常...");
        }

        return finalResult;
    }

    private Callable<Map<String, List<PaymentRecordInfo>>> queryPaymentTask(final String mobile, final String month){

        Callable task = new Callable() {

            public Object call() throws Exception {

                int tryTimes = 0;
                Map<String, List<PaymentRecordInfo>> result = new HashMap<String, List<PaymentRecordInfo>>();
                List<PaymentRecordInfo> value = new ArrayList<PaymentRecordInfo>();

                while(tryTimes < 3){

                    try{
                        tryTimes++;
                        value = queryChargeInfo(mobile, month, true);
                        break;
                    }catch(ApiException e){
                        if("1002".equals(e.getErrCode())){
                            System.out.println(e.getMessage());
                            Thread.sleep(1000 * 10);
                        }else{
                            throw e;
                        }
                    }
                }

                result.put(mobile, value);
                return result;
            }
        };
        return task;
    }

    /**
     * 添加新号码到缓存中
     * @param mobiles
     */
    public void addCacheMobiles(String mobiles) {
        if(mobiles == null){
            throw new ApiException("批量查询错误 : 参数 mobiles = null");
        }
        String[] mobileArr = mobiles.split(",");
        Map<String, CacheResult> newCache = new HashMap<String, CacheResult>();

        for(String mobile : mobileArr){
            if(CacheCenter.resultMap.containsKey(mobile)){
                continue;
            }
            newCache.put(mobile, new CacheResult());
        }
        newCache.putAll(CacheCenter.resultMap);
        CacheCenter.resultMap = newCache;
    }
}