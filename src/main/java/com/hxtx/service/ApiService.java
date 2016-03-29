package com.hxtx.service;

import com.hxtx.entity.Balance;
import com.hxtx.entity.FlowSet;
import com.hxtx.entity.PaymentRecordInfo;
import com.hxtx.entity.SubAccuInfo;
import com.hxtx.exception.ApiException;
import org.apache.axis2.context.externalize.ActivateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 实现api服务的具体业务逻辑
 * Created by dongchen on 16/3/27.
 */
@Service
public class ApiService {

    private final String SuccCode = "0000";

    @Autowired
    ExchangeService exchange;

    /**
     * 查询手机号余额
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
     * 查询手机流量套餐
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
}