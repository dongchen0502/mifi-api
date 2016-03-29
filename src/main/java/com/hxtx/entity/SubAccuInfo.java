package com.hxtx.entity;

/**
 * 子套餐详细信息
 * Created by dongchen on 16/3/30.
 */
public class SubAccuInfo {
    private String accuName;
    private String startTime;
    private String endTime;
    private String accuAmount;
    private String usedAmount;
    private String transferAccuAmount;
    private String transferUsedAmount;
    private String unitTypeId;


    public String getAccuName() {
        return accuName;
    }

    public void setAccuName(String accuName) {
        this.accuName = accuName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAccuAmount() {
        return accuAmount;
    }

    public void setAccuAmount(String accuAmount) {
        this.accuAmount = accuAmount;
    }

    public String getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(String usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getTransferAccuAmount() {
        return transferAccuAmount;
    }

    public void setTransferAccuAmount(String transferAccuAmount) {
        this.transferAccuAmount = transferAccuAmount;
    }

    public String getTransferUsedAmount() {
        return transferUsedAmount;
    }

    public void setTransferUsedAmount(String transferUsedAmount) {
        this.transferUsedAmount = transferUsedAmount;
    }

    public String getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(String unitTypeId) {
        this.unitTypeId = unitTypeId;
    }
}
