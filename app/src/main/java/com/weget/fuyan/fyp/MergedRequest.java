package com.weget.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 7/11/2016.
 */
public class MergedRequest extends Request implements Serializable {

    int count;

    public MergedRequest(int imageResource, String productName, String requirement, int count){
        super(imageResource, productName, requirement);
        this.count = count;
    }

    public MergedRequest(int id, int requestorId, int imageResource, String productName, String requirement, String location, int postal, String startTime,
                   String endTime, int duration, double price, String status, int count){
        super(id, requestorId, imageResource, productName, requirement, location, postal, startTime, endTime, duration, price, status);
        this.count = count;

    }

    public int getFulfillerCount() {
        return count;
    }

    public void setFulfillerCount(int count) {
        this.count = count;
    }
}
