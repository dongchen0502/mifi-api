package com.hxtx.api;

import com.hub.chinatelecom.www.Exchange;
import com.hub.chinatelecom.www.ExchangeResponse;
import com.hub.chinatelecom.www.IDEPService;
import com.hub.chinatelecom.www.IDEPServiceStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 提供所有对外服务的api, 数据输出格式json
 * Created by dongchen on 16/3/27.
 */
@Controller
public class ApiController {

    @Autowired
    private ApiService apiService;

    @RequestMapping(value = "/balance", method = {RequestMethod.GET})
    public String queryBalance(String mobile, int queryType, Model model){
        System.out.println("@RequestMapping(value = \"/yue\", method = {RequestMethod.GET})");
        //todo 根据mobile & type 发起查询请求 返回xml
        try {
            IDEPService service = new IDEPServiceStub();
            Exchange exchange = new Exchange();
            String trans_id = SrcSysID+sdf.format(System.currentTimeMillis())+sdf5.format(System.currentTimeMillis());
            String xml_str = "<?xml version=\"1.0\" encoding=\"UTF8\"?>"+
                    "<ContractRoot>" +
                    "<TcpCont>" +
                    "<TransactionID>"+trans_id+"</TransactionID>" +
                    "<ActionCode>0</ActionCode>" +
                    "<BusCode>BUS81000</BusCode>" +
                    "<ServiceCode>SVC81001</ServiceCode>" +
                    "<ServiceContractVer>SVC1100120110501</ServiceContractVer>"+
                    "<ServiceLevel>1</ServiceLevel>" +
                    "<SrcOrgID>"+SrcOrgID+"</SrcOrgID>" +
                    "<SrcSysID>"+SrcSysID+"</SrcSysID>" +
                    "<SrcSysSign>integral10000000830803</SrcSysSign>"+
                    "<DstOrgID>"+DstOrgID+"</DstOrgID>" +
                    "<DstSysID>"+DstSysID+"</DstSysID>" +
                    "<ReqTime>"+sdf2.format(System.currentTimeMillis())+"</ReqTime>" +
                    "</TcpCont>" +
                    "<SvcCont>" +
                    "<CustMobile>"+mobile+"</CustMobile>" +
                    "<QueryType>"+queryType+"</QueryType>" +
                    "</SvcCont>" +
                    "</ContractRoot>";
            exchange.setIn0(xml_str);
            System.out.println("balance_str: "+xml_str);
            ExchangeResponse response   = service.exchange(exchange);
            String resp_xml_str = response.getOut();
            System.out.println("balance_response_str: " +resp_xml_str);
//            SAXReader reader = new SAXReader();
//            Document doc = DocumentHelper.parseText(resp_xml_str);
//            Element root  = doc.getRootElement();
//            Element TcpCont = root.element("TcpCont");
//            Element transaction_id = TcpCont.element("TransactionID");
//            String response_code = TcpCont.element("Response").element("RspCode").getText();
//            if("0000".equals(response_code)||response_code=="0000"){
//                BalanceForm   form = new BalanceForm();
//                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");
//                String balanceAmount  = qryInfoRsp .element("BalanceAmount").getText();
//                String shouldCharge = qryInfoRsp.element("BalanceAmount").getText();
//                String sumCharge = qryInfoRsp.element("SumCharge").getText();
//                form.setBalanceAmount(Double.parseDouble(balanceAmount));
//                form.setShouldCharge(Double.parseDouble(shouldCharge));
//                form.setSumCharge(Double.parseDouble(sumCharge));
//                return form;
//            }else{
//                //打印日志
//                return null;
//            }
            model.addAttribute("response", resp_xml_str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return "response";
    }

    @RequestMapping(value = "/flowSet", method = {RequestMethod.GET})
    public String queryTaocan(String mobile, String month, Model model){
        try {
            IDEPService service = new IDEPServiceStub();
            Exchange exchange = new Exchange();
            String trans_id = SrcSysID+sdf.format(System.currentTimeMillis())+sdf5.format(System.currentTimeMillis());
            month = month.replace("-", "");
            String xml_str = "<?xml version=\"1.0\" encoding=\"UTF8\"?>"+
                    "<ContractRoot>" +
                    "<TcpCont>" +
                    "<TransactionID>"+trans_id+"</TransactionID>" +
                    "<ActionCode>0</ActionCode>" +
                    "<BusCode>BUS81000</BusCode>" +
                    "<ServiceCode>SVC81003</ServiceCode>" +
                    "<ServiceContractVer>SVC1100120110501</ServiceContractVer>"+
                    "<ServiceLevel>1</ServiceLevel>" +
                    "<SrcOrgID>"+SrcOrgID+"</SrcOrgID>" +
                    "<SrcSysID>"+SrcSysID+"</SrcSysID>" +
                    "<SrcSysSign>integral10000000830803</SrcSysSign>"+
                    "<DstOrgID>"+DstOrgID+"</DstOrgID>" +
                    "<DstSysID>"+DstSysID+"</DstSysID>" +
                    "<ReqTime>"+sdf2.format(System.currentTimeMillis())+"</ReqTime>" +
                    "</TcpCont>" +
                    "<SvcCont>" +
                    "<CustMobile>"+mobile+"</CustMobile>" +
                    "<Month>"+month+"</Month>" +
                    "</SvcCont>" +
                    "</ContractRoot>";
            exchange.setIn0(xml_str);
            System.out.println(xml_str);
            ExchangeResponse response   = service.exchange(exchange);
            String resp_xml_str = response.getOut();
            System.out.println(resp_xml_str);
//            SAXReader reader = new SAXReader();
//            Document doc = DocumentHelper.parseText(resp_xml_str);
//            Element root  = doc.getRootElement();
//            Element TcpCont = root.element("TcpCont");
//            Element transaction_id = TcpCont.element("TransactionID");
//            String response_code = TcpCont.element("Response").element("RspCode").getText();
//
//            if("0000".equals(response_code)||response_code=="0000"){
//                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");
//                Element typeId = qryInfoRsp.element("InfoTypeID");
//                if(typeId.getText().trim() =="13"||"13".equals(typeId.getText().trim())){
//                    form = new SetsInfoForm();
//                    Element accuInfo = qryInfoRsp.element("PPAccuInfo").element("SubAccuInfo").element("AccuInfo");
//                    String start_time = accuInfo.element("StartTime").getText().trim();
//                    String end_time = accuInfo.element("EndTime").getText().trim();
//                    String accuamount  = accuInfo.element("AccuAmount").getText().trim();
//                    String usedAmount = accuInfo.element("UsedAmount").getText().trim();
//                    String transferAccuAmount = accuInfo.element("TransferAccuAmount").getText().trim();
//                    String transferUsedAmount = accuInfo.element("TransferUsedAmount").getText().trim();
//                    String unitTypeId = accuInfo.element("UnitTypeId").getText().trim();
//                    String accu_name = accuInfo.element("accu_name").getText().trim();
//                    form.setAcc_name(accu_name);
//                    form.setAccountAmount(Double.parseDouble(accuamount));
//                    form.setEnd_time(end_time);
//                    form.setMobile_num(mobile);
//                    form.setStart_time(start_time);
//                    form.setTransferAccuAmount(Double.parseDouble(transferAccuAmount));
//                    form.setTransferUsedAmount(Double.parseDouble(transferUsedAmount));
//                    form.setUnitTypeId(unitTypeId);
//                    form.setUsedAmount(Double.parseDouble(usedAmount));
//                    form.setQuery_time(sdf4.format(System.currentTimeMillis()));
//
//                }
//            }
            model.addAttribute("response", resp_xml_str);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "response";
    }

    @RequestMapping(value = "/chargeInfo", method = {RequestMethod.GET})
    public String queryChargeInfo(String mobile, String month, Model model){

        try {
            month = month.replace("-", "");
            IDEPService service = new IDEPServiceStub();
            Exchange exchange = new Exchange();
            String trans_id = SrcSysID+sdf.format(System.currentTimeMillis())+sdf5.format(System.currentTimeMillis());
            String xml_str = "<?xml version=\"1.0\" encoding=\"UTF8\"?>"+
                    "<ContractRoot>" +
                    "<TcpCont>" +
                    "<TransactionID>"+trans_id+"</TransactionID>" +
                    "<ActionCode>0</ActionCode>" +
                    "<BusCode>BUS81000</BusCode>" +
                    "<ServiceCode>SVC81006</ServiceCode>" +
                    "<ServiceContractVer>SVC1100120110501</ServiceContractVer>"+
                    "<ServiceLevel>1</ServiceLevel>" +
                    "<SrcOrgID>"+SrcOrgID+"</SrcOrgID>" +
                    "<SrcSysID>"+SrcSysID+"</SrcSysID>" +
                    "<SrcSysSign>integral10000000830803</SrcSysSign>"+
                    "<DstOrgID>"+DstOrgID+"</DstOrgID>" +
                    "<DstSysID>"+DstSysID+"</DstSysID>" +
                    "<ReqTime>"+sdf2.format(System.currentTimeMillis())+"</ReqTime>" +
                    "</TcpCont>" +
                    "<SvcCont>" +
                    "<CustMobile>"+mobile+"</CustMobile>" +
                    "<Month>"+month+"</Month>" +
                    "</SvcCont>" +
                    "</ContractRoot>";
            exchange.setIn0(xml_str);
            System.out.println(xml_str);
            ExchangeResponse response   = service.exchange(exchange);
            String resp_xml_str = response.getOut();
            System.out.println(resp_xml_str);
//            SAXReader reader = new SAXReader();
//            Document doc = DocumentHelper.parseText(resp_xml_str);
//            Element root  = doc.getRootElement();
//            Element TcpCont = root.element("TcpCont");
//            Element transaction_id = TcpCont.element("TransactionID");
//            String response_code = TcpCont.element("Response").element("RspCode").getText();
//            if("0000".equals(response_code)||response_code=="0000"){
//                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");
//                Element typeId = qryInfoRsp.element("InfoTypeID");
//                if(typeId.getText().trim() =="16"||"16".equals(typeId.getText().trim())){
//                    form = new ChargeInfoForm();
//                    Element paymentRecordInfo = qryInfoRsp.element("PaymentRecordInfo").element("PaymentRecordInfo");
//                    String paymentAmount  = paymentRecordInfo.element("PaymentAmount").getText().trim();
//                    String paymentMethod  = paymentRecordInfo.element("PaymentMethod").getText().trim();
//                    String payTime = paymentRecordInfo.element("PayTime").getText().trim();
//                    form.setMobile_num(mobile);
//                    form.setPaymentAmount(paymentAmount);
//                    form.setPaymentMethod(paymentMethod);
//                    form.setPayTime(payTime);
//                    form.setQuery_time(sdf4.format(System.currentTimeMillis()));
//                    return form;
//                }
//            }
            model.addAttribute("response", resp_xml_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "response";
    }
}
