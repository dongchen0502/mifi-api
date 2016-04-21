package com.hxtx.service;

import com.hxtx.entity.*;
import com.hxtx.exception.ApiException;
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
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 查询手机号余额, 网关接口有限速,一秒钟超过10次查询则会异常
     *
     * @param mobile    查询的手机号码
     * @param queryType 查询类型
     * @return Balance
     */
    public Balance queryBalance(String mobile, int queryType) {
        Balance result = null;

        String xml = exchange.balance(mobile, queryType);

        if (StringUtils.isEmpty(xml)) {
            return result;
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
    public List<FlowSet> queryFlowSet(String mobile, String month) {
        List<FlowSet> result = new ArrayList<FlowSet>();

        month = month.replace("-", "");

        String xml = exchange.flowSet(mobile, month);
        System.out.println(mobile + " | " + month + " queryFlowSet resp: \n" + xml);

        if (StringUtils.isEmpty(xml)) {
            return result;
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
                throw new ApiException(respCode + ":" + rspDesc);
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
    public List<PaymentRecordInfo> queryChargeInfo(String mobile, String month) {
        List<PaymentRecordInfo> result = new ArrayList<PaymentRecordInfo>();

        month = month.replace("-", "");
        String xml = exchange.chargeInfo(mobile, month);
        System.out.println(mobile + " | " + month + " queryChargeInfo resp: \n" + xml);

        if (StringUtils.isEmpty(xml)) {
            return result;
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
                throw new ApiException(respCode + ":" + rspDesc);
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
            completionService.submit(queryTask(mobile, month));
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

    private Callable<FlowSetProfile> queryTask(final String mobile, final String month){

        Callable task = new Callable() {

            public Object call() throws Exception {

                List<FlowSet> flowSet = queryFlowSet(mobile, month);

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
}