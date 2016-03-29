package com.hxtx.api;

import com.hxtx.entity.Balance;
import com.hxtx.entity.FlowSet;
import com.hxtx.entity.HttpResult;
import com.hxtx.entity.PaymentRecordInfo;
import com.hxtx.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 提供所有对外服务的api, 数据输出格式json
 * Created by dongchen on 16/3/27.
 */
@Controller
public class ApiController extends BaseController{

    @Autowired
    private ApiService apiService;

    @RequestMapping(value = "/balance", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryBalance(String mobile, int queryType) {
        Balance balance = apiService.queryBalance(mobile, queryType);
        return HttpResult.succResult(balance);
    }

    @RequestMapping(value = "/flowset", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryFlowSet(String mobile, String month) {
        List<FlowSet> flowSets = apiService.queryFlowSet(mobile, month);
        return HttpResult.succResult(flowSets);
    }

    @RequestMapping(value = "/chargeInfo", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryChargeInfo(String mobile, String month){
        List<PaymentRecordInfo> chargeInfo = apiService.queryChargeInfo(mobile, month);
        return HttpResult.succResult(chargeInfo);
    }
}
