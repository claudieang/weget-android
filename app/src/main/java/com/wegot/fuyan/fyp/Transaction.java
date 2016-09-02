package com.wegot.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 8/3/2016.
 */
public class Transaction implements Serializable {

    private int transactionId, requestId, fulfillId;
    boolean received, delivered;
    double amount;
    String brainTreeCode, status;

    public Transaction(int transactionId, int requestId, int fulfillId,
                       double amount, String brainTreeCode, boolean received,
                       boolean delivered, String status){
        this.transactionId = transactionId;
        this.requestId = requestId;
        this.fulfillId = fulfillId;
        this.amount = amount;
        this.brainTreeCode = brainTreeCode;
        this.received = received;
        this.delivered = delivered;
        this.status = status;

    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getFulfillId() {
        return fulfillId;
    }

    public void setFulfillId(int fulfillId) {
        this.fulfillId = fulfillId;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBrainTreeCode() {
        return brainTreeCode;
    }

    public void setBrainTreeCode(String brainTreeCode) {
        this.brainTreeCode = brainTreeCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
