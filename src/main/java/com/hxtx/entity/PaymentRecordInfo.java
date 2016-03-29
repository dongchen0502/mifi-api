package com.hxtx.entity;

/**
 * 一次充值记录对应的对象
 * Created by dongchen on 16/3/30.
 */
public class PaymentRecordInfo {

    private String paymentAmount;
    private String paymentMethod;
    private String payTime;

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
}
