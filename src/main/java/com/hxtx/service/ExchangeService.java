package com.hxtx.service;

import com.hub.chinatelecom.www.Exchange;
import com.hub.chinatelecom.www.ExchangeResponse;
import com.hub.chinatelecom.www.IDEPService;
import com.hub.chinatelecom.www.IDEPServiceStub;
import org.apache.axis2.AxisFault;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 和网关实际交户的业务逻辑
 * Created by dongchen on 16/3/27.
 */
@Service
public class ExchangeService {
    private String DstSysID = "6090010003";
    private String SrcOrgID = "609002";
    private String SrcSysID = "6090010002";
    private String DstOrgID = "609001";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMM");
    private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf5 = new SimpleDateFormat("MMddHHmmss");

    IDEPService service = null;
    Exchange exchange = null;

    public ExchangeService() {
        try {
            this.service = new IDEPServiceStub();
            this.exchange = new Exchange();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
    }

    /**
     * TransactionID 长度28
     * 【10位系统/平台编码代码，见附录1 SrcSysID字段】+【8位日期编码YYYYMMDD】＋【10位流水号】
     *
     * @return
     */
    private String getTransId() {
        return SrcSysID + sdf.format(System.currentTimeMillis()) + sdf5.format(System.currentTimeMillis());
    }

    /**
     * 构建交换数据的xml格式
     * @param busCode
     * @param SvcCode
     * @param svcContentMap
     * @return
     */
    private String buildExchangeXML(String busCode, String SvcCode, Map<String, String> svcContentMap){
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("UTF-8");
        //生成根节点
        Element root = document.addElement("ContractRoot");
        //tcpCont节点内容
        Element tcpCont = root.addElement("TcpCont");
        tcpCont.addElement("TransactionID").addText(this.getTransId());
        tcpCont.addElement("ActionCode").addText("0");
        tcpCont.addElement("BusCode").addText(busCode);
        tcpCont.addElement("ServiceCode").addText(SvcCode);
        tcpCont.addElement("ServiceContractVer").addText("SVC1100120110501");
        tcpCont.addElement("ServiceLevel").addText("1");
        tcpCont.addElement("SrcOrgID").addText(SrcOrgID);
        tcpCont.addElement("SrcSysID").addText(SrcSysID);
        tcpCont.addElement("SrcSysSign").addText("integral10000000830803");
        tcpCont.addElement("DstOrgID").addText(DstOrgID);
        tcpCont.addElement("DstSysID").addText(DstSysID);
        tcpCont.addElement("ReqTime").addText(sdf2.format(System.currentTimeMillis()));

        //svcCont节点内容
        Element svcCont = root.addElement("SvcCont");
        for(String nodeName : svcContentMap.keySet()){
            svcCont.addElement(nodeName).addText(svcContentMap.get(nodeName));
        }

        return document.asXML();
    }

    /**
     * 查询账户余额
     * @param mobile
     * @param queryType
     * @return 返回原生xml, 异常发生时,返回空串
     */
    public String balance(String mobile, int queryType) {
        String result = "";

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("QueryType", String.valueOf(queryType));

        String exchangeXML = this.buildExchangeXML("BUS81000", "SVC81001", params);

        exchange.setIn0(exchangeXML);

        try {
            //阻塞操作, 将来改进为线程池
            ExchangeResponse response = service.exchange(exchange);
            result = response.getOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查询流量套餐
     * @param mobile
     * @param month
     * @return
     */
    public String flowSet(String mobile, String month) {
        String result = "";

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("Month", month);

        String exchangeXML = this.buildExchangeXML("BUS81000", "SVC81003", params);

        exchange.setIn0(exchangeXML);

        try {
            //阻塞操作, 将来改进为线程池
            ExchangeResponse response = service.exchange(exchange);
            result = response.getOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查询交费历史
     * @param mobile
     * @param month
     * @return
     */
    public String chargeInfo(String mobile, String month){
        String result = "";

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("Month", month);

        String exchangeXML = this.buildExchangeXML("BUS81000", "SVC81006", params);

        exchange.setIn0(exchangeXML);

        try {
            //阻塞操作, 将来改进为线程池
            ExchangeResponse response = service.exchange(exchange);
            result = response.getOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return result;
    }
}
