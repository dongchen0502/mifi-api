package com.hxtx.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongchen on 16/5/3.
 */
public class CacheResult implements Serializable{
    private String balance;
    private Map<String, String> flowset = new HashMap<String, String>();
    private Map<String, String> payment = new HashMap<String, String>();

    public String getFlowsetByMonth(String month){
        if(flowset.containsKey(month)){
            return flowset.get(month);
        }else{
            return "";
        }
    }

    public String getPaymentByMonth(String month){
        if(payment.containsKey(month)){
            return payment.get(month);
        }else{
            return "";
        }
    }

    public void setFlowset(String month, String result){
        flowset.put(month, result);
    }

    public void setPayment(String month, String result){
        flowset.put(month, result);
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Map<String, String> getFlowset() {
        return flowset;
    }

    public void setFlowset(Map<String, String> flowset) {
        this.flowset = flowset;
    }

    public Map<String, String> getPayment() {
        return payment;
    }

    public void setPayment(Map<String, String> payment) {
        this.payment = payment;
    }
}
