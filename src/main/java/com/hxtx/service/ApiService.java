package com.hxtx.service;

import com.hxtx.entity.Balance;
import com.hxtx.exception.ApiException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @param mobile 查询的手机号码
     * @param queryType 查询类型
     * @return Balance
     */
    public Balance queryBalance(String mobile, int queryType) {
        Balance result = null;

        String xml = exchange.balance(mobile, queryType);

        if(StringUtils.isEmpty(xml)){
            return result;
        }

        try{
            Document doc = DocumentHelper.parseText(xml);
            Element root  = doc.getRootElement();
            Element TcpCont = root.element("TcpCont");
            String respCode = TcpCont.element("Response").element("RspCode").getText();

            if(SuccCode.equals(respCode)){
                Element qryInfoRsp = root.element("SvcCont").element("QryInfoRsp");
                String ba  = qryInfoRsp.element("BalanceAmount").getText();
                String shouldCharge = qryInfoRsp.element("ShouldCharge").getText();
                String sum = qryInfoRsp.element("SumCharge").getText();

                result = new Balance(ba, shouldCharge, sum);
            }else{
                String rspDesc = TcpCont.element("Response").element("RspDesc").getText();
                throw new ApiException(respCode + ":" + rspDesc);
            }
        }catch(DocumentException e){
            e.printStackTrace();
        }
        return result;
    }
}
