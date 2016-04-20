package com.hxtx.api;

import com.hxtx.entity.*;
import com.hxtx.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    /**
     * 批量查询流量套餐接口
     * @param mobiles 逗号分隔的
     * @param month 查询月份
     * @return 每个号码对应的套餐的概要信息,详细信息请走单个号码查询
     */
    @RequestMapping(value = "/flowset", method = {RequestMethod.POST})
    @ResponseBody
    public Object batchQueryFlowSet(String mobiles, String month) {
        Map<String, FlowSetProfile> flowSets = apiService.batchQueryFlowSet(mobiles, month);
        return HttpResult.succResult(flowSets);
    }
}
