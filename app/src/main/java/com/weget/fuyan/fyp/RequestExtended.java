package com.weget.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 10/29/2016.
 */

public class RequestExtended extends Request implements Serializable {
    boolean credited;



    public RequestExtended(int id, int requestorId, int imageResource, String productName, String requirement, String location, int postal, String startTime,
                           String endTime, int duration, double price, String status, boolean credited) {
        super(id, requestorId, imageResource, productName, requirement, location, postal, startTime, endTime, duration, price,status);
        this.credited = credited;


    }public boolean isCredited() {
        return credited;
    }

    public void setCredited(boolean credited) {
        this.credited = credited;
    }


}
