package com.hxtx.entity;

import java.util.List;

/**
 * 流量查询结果的一个父套餐对象
 * Created by dongchen on 16/3/29.
 */
public class FlowSet {
    /**
     * 父套餐名称
     */
    private String prodOffName;
    private String pStartTime;
    private String pEndTime;

    /**
     * 子套餐集合
     */
    private List<SubAccuInfo> subAccuInfoList;


    public String getProdOffName() {
        return prodOffName;
    }

    public void setProdOffName(String prodOffName) {
        this.prodOffName = prodOffName;
    }

    public String getpStartTime() {
        return pStartTime;
    }

    public void setpStartTime(String pStartTime) {
        this.pStartTime = pStartTime;
    }

    public String getpEndTime() {
        return pEndTime;
    }

    public void setpEndTime(String pEndTime) {
        this.pEndTime = pEndTime;
    }

    public List<SubAccuInfo> getSubAccuInfoList() {
        return subAccuInfoList;
    }

    public void setSubAccuInfoList(List<SubAccuInfo> subAccuInfoList) {
        this.subAccuInfoList = subAccuInfoList;
    }
}
