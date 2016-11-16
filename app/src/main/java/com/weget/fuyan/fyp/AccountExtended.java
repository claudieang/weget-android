
package com.weget.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 7/13/2016.
 */
public class AccountExtended extends Account implements Serializable {
    double requestTotal, requestNo, fulfillTotal, fulfillNo, requestMade, fulfillMade;

    public AccountExtended(int id, String username, String password, int contactNo, String email, String fulfiller, String picture, double requestTotal, double requestNo, double fulfillTotal, double fulfillNo, double requestMade, double fulfillMade) {
        super(id, username, password, contactNo, email, fulfiller, picture);
        this.requestTotal = requestTotal;
        this.requestNo = requestNo;
        this.fulfillTotal = fulfillTotal;
        this.fulfillNo = fulfillNo;
        this.requestMade = requestMade;
        this.fulfillMade = fulfillMade;
    }

    public double getRequestTotal() {
        return requestTotal;
    }

    public void setRequestTotal(int requestTotal) {
        this.requestTotal = requestTotal;
    }

    public double getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(int requestNo) {
        this.requestNo = requestNo;
    }

    public double getFulfillTotal() {
        return fulfillTotal;
    }

    public void setFulfillTotal(int fulfillTotal) {
        this.fulfillTotal = fulfillTotal;
    }

    public double getFulfillNo() {
        return fulfillNo;
    }

    public void setFulfillNo(int fulfillNo) {
        this.fulfillNo = fulfillNo;
    }

    public double getRequestMade() {
        return requestMade;
    }

    public void setRequestMade(int requestMade) {
        this.requestMade = requestMade;
    }

    public double getFulfillMade() {
        return fulfillMade;
    }

    public void setFulfillMade(int fulfillMade) {
        this.fulfillMade = fulfillMade;
    }
}

