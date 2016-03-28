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
     * 查询账户余额
     * @param mobile
     * @param queryType
     * @return 返回原生xml, 异常发生时,返回空串
     */
    public String balance(String mobile, int queryType) {
        String result = "";

        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("UTF-8");
        //生成根节点
        Element root = document.addElement("ContractRoot");
        //tcpCont节点内容
        Element tcpCont = root.addElement("TcpCont");
        tcpCont.addElement("TransactionID").setData(this.getTransId());
        tcpCont.addElement("ActionCode").setData(0);
        tcpCont.addElement("BusCode").setData("BUS81000");
        tcpCont.addElement("ServiceCode").setData("SVC81001");
        tcpCont.addElement("ServiceContractVer").setData("SVC1100120110501");
        tcpCont.addElement("ServiceLevel").setData("1");
        tcpCont.addElement("SrcOrgID").setData(SrcOrgID);
        tcpCont.addElement("SrcSysID").setData(SrcSysID);
        tcpCont.addElement("SrcSysSign").setData("integral10000000830803");
        tcpCont.addElement("DstOrgID").setData(DstOrgID);
        tcpCont.addElement("DstSysID").setData(DstSysID);
        tcpCont.addElement("ReqTime").setData(sdf2.format(System.currentTimeMillis()));

        //svcCont节点内容
        Element svcCont = root.addElement("SvcCont");
        svcCont.addElement("CustMobile").setData(mobile);
        svcCont.addElement("QueryType").setData(queryType);
        exchange.setIn0(document.getStringValue());

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
