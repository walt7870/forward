package com.ntech.model;

import java.math.BigDecimal;
import java.util.Date;

public class OrderInfo extends OrderInfoKey {
    private String method;

    private BigDecimal amount;

    private Byte status;

    private Date orderTime;

    private Date payTime;

    private Date refundsTime;

    private String contype;

    private Integer value;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getRefundsTime() {
        return refundsTime;
    }

    public void setRefundsTime(Date refundsTime) {
        this.refundsTime = refundsTime;
    }

    public String getContype() {
        return contype;
    }

    public void setContype(String contype) {
        this.contype = contype == null ? null : contype.trim();
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}