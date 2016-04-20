package com.hxtx.entity;

/**
 * 流量概要信息
 * Created by dongchen on 16/4/20.
 */
public class FlowSetProfile {
    private String mobile;
    private String month;
    private double totalTrafficLeft;
    private double monthTrafficLeft;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getTotalTrafficLeft() {
        return totalTrafficLeft;
    }

    public void setTotalTrafficLeft(double totalTrafficLeft) {
        this.totalTrafficLeft = totalTrafficLeft;
    }

    public double getMonthTrafficLeft() {
        return monthTrafficLeft;
    }

    public void setMonthTrafficLeft(double monthTrafficLeft) {
        this.monthTrafficLeft = monthTrafficLeft;
    }
}
