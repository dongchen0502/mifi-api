package com.hxtx.utils;

import com.hxtx.constants.ExchangeCodes;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by dongchen on 16/5/6.
 */
public class ExchangeUtils {
    /**
     * 解析网关返回状态码
     */
    public static String parseCode(String xml){
        String code = ExchangeCodes.UNKNOWN;
        if(StringUtils.isNotEmpty(xml)){
            try {
                Document doc = DocumentHelper.parseText(xml);
                Element root = doc.getRootElement();
                Element TcpCont = root.element("TcpCont");

                code = TcpCont.element("Response").element("RspCode").getText();

            } catch (DocumentException e) {
                code = ExchangeCodes.ERROR_FORMAT;
                e.printStackTrace();
            }
        }
        return code;
    }

    /**
     * 判断网关返回码是否成功
     * @param code
     * @return
     */
    public static boolean isSuccCode(String code){
        return ExchangeCodes.SUCCESS.equals(code);
    }

    public static boolean isSuccResp(String xml){
        String code = ExchangeUtils.parseCode(xml);
        return ExchangeUtils.isSuccCode(code);
    }

    public static void main(String [] a){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ContractRoot><TcpCont><TransactionID>6090010002201603270327142652</TransactionID><ActionCode>1</ActionCode><RspTime>20160327142909</RspTime><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>成功</RspDesc></Response></TcpCont><SvcCont><QryInfoRsp><InfoTypeID>11</InfoTypeID><BalanceAmount>0</BalanceAmount><ShouldCharge>2000</ShouldCharge><SumCharge>2000</SumCharge></QryInfoRsp></SvcCont></ContractRoot>";
        long t = System.currentTimeMillis();
        String code = ExchangeUtils.parseCode(xml);
        System.out.println(code);
        System.out.println(System.currentTimeMillis() - t);
        ExchangeUtils.parseCode(xml);
        System.out.println(code);
        System.out.println(System.currentTimeMillis() - t);
    }
}
