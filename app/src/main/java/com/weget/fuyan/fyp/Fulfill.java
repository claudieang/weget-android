package com.weget.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 8/1/2016.
 */
public class Fulfill implements Serializable {
    private int id, requestId, fulfillerId;
    private String status;

    public Fulfill (int id, int requestId, int fulfillerId, String status){
        this.id = id;
        this.requestId = requestId;
        this.fulfillerId = fulfillerId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getFulfillerId() {
        return fulfillerId;
    }

    public void setFulfillerId(int fulfillerId) {
        this.fulfillerId = fulfillerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
