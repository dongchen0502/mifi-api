package com.hxtx.entity;

/**
 * 余额查询结果实体类
 * Created by dongchen on 16/3/27.
 */
public class Balance {
    /**
     * 余额, 单位:分
     */
    private String balanceAmount;
    /**
     * 欠费应收金额, 单位:分
     */
    private String shouldCharge;
    /**
     * 实时话费, 单位:分
     */
    private String sumCharge;

    public Balance(String balanceAmount, String shouldCharge, String sumCharge) {
        this.balanceAmount = balanceAmount;
        this.shouldCharge = shouldCharge;
        this.sumCharge = sumCharge;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getShouldCharge() {
        return shouldCharge;
    }

    public void setShouldCharge(String shouldCharge) {
        this.shouldCharge = shouldCharge;
    }

    public String getSumCharge() {
        return sumCharge;
    }

    public void setSumCharge(String sumCharge) {
        this.sumCharge = sumCharge;
    }
}
