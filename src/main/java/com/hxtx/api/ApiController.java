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
        Balance balance = apiService.queryBalance(mobile, queryType, false);
        return HttpResult.succResult(balance);
    }

    @RequestMapping(value = "/flowset", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryFlowSet(String mobile, String month) {
        List<FlowSet> flowSets = apiService.queryFlowSet(mobile, month, false);
        return HttpResult.succResult(flowSets);
    }

    @RequestMapping(value = "/chargeInfo", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryChargeInfo(String mobile, String month){
        List<PaymentRecordInfo> chargeInfo = apiService.queryChargeInfo(mobile, month, false);
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

    /**
     * 批量查询流量套餐接口
     * @param mobiles 逗号分隔
     * @param month 查询月份
     * @return 每个号码对应月份的充值详情
     */
    @RequestMapping(value = "/chargeInfo", method = {RequestMethod.POST})
    @ResponseBody
    public Object batchQueryChargeInfo(String mobiles, String month){
        Map<String, List<PaymentRecordInfo>> chargeInfos = apiService.batchQueryChangeInfo(mobiles, month);
        return HttpResult.succResult(chargeInfos);
    }

    /**
     * 添加一批新的号码到缓存,方便以后查询可以快速得到结果
     * @param mobiles
     * @return
     */
    @RequestMapping(value = "/mobiles/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object addCacheMobiles(String mobiles){
        apiService.addCacheMobiles(mobiles);
        return HttpResult.succResult("ok");
    }
}
