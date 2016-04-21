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
import java.util.concurrent.ConcurrentHashMap;

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
    private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    private static SimpleDateFormat sdf5 = new SimpleDateFormat("MMddHHmmss");

    private static int reqInterval = 500;

    private static final String TYPE_BALANCE = "SVC81001";
    private static final String TYPE_FLOWSET = "SVC81003";
    private static final String TYPE_PAYMENT = "SVC81006";

    private static final String BUS_CODE = "BUS81000";

    /**
     * 保存上次调用接口的时间戳, 每种类型的接口每秒钟可访问10次
     */
    private Map<String, Long> speedController;

    IDEPService service = null;

    public ExchangeService() {
        try {
            this.service = new IDEPServiceStub();

            speedController = new ConcurrentHashMap<String, Long>(3);
            speedController.put(TYPE_BALANCE, System.currentTimeMillis());
            speedController.put(TYPE_FLOWSET, System.currentTimeMillis());
            speedController.put(TYPE_PAYMENT, System.currentTimeMillis());

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
    }

    private void waitIfNeeded(String queryType){
        synchronized (queryType){
            long waitMill = reqInterval - (System.currentTimeMillis() - speedController.get(queryType));
            System.out.println(Thread.currentThread().getName() + " thread exchange " + queryType + " at " + sdf4.format(System.currentTimeMillis()) + " need wait : " + waitMill);
            if(waitMill > 0){
                try {
                    Thread.sleep(waitMill);
                } catch (InterruptedException e) {
                }
            }
            speedController.put(queryType, System.currentTimeMillis());
        }
    }

    /**
     * 请求网关接口的一个封装, 由于每类型接口有限速(10 / 秒), 在这里做一个速度控制
     * @param exchangeXML
     * @param svc
     * @return
     */
    private String exchangeWarp(String exchangeXML, String svc){
        String result = "";
        try {
            Exchange exchange = new Exchange();
            exchange.setIn0(exchangeXML);

            waitIfNeeded(svc);
            //阻塞操作, 将来改进为线程池
            ExchangeResponse response = service.exchange(exchange);
            result = response.getOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
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

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("QueryType", String.valueOf(queryType));

        String exchangeXML = this.buildExchangeXML(BUS_CODE, TYPE_BALANCE, params);

        return this.exchangeWarp(exchangeXML, TYPE_BALANCE);
    }

    /**
     * 查询流量套餐
     * @param mobile
     * @param month
     * @return 返回原生xml, 异常发生时,返回空串
     */
    public String flowSet(String mobile, String month) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("Month", month);

        String exchangeXML = this.buildExchangeXML(BUS_CODE, TYPE_FLOWSET, params);
        return this.exchangeWarp(exchangeXML, TYPE_FLOWSET);
    }

    /**
     * 查询交费历史
     * @param mobile
     * @param month
     * @return 返回原生xml, 异常发生时,返回空串
     */
    public String chargeInfo(String mobile, String month){

        Map<String, String> params = new HashMap<String, String>();
        params.put("CustMobile", mobile);
        params.put("Month", month);

        String exchangeXML = this.buildExchangeXML(BUS_CODE, TYPE_PAYMENT, params);
        return this.exchangeWarp(exchangeXML, TYPE_PAYMENT);
    }
}
