
package com.weget.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 7/13/2016.
 */
public class AccountExtended extends Account implements Serializable {
    int requestTotal, requestNo, fulfillTotal, fulfillNo, requestMade, fulfillMade;

    public AccountExtended(int id, String username, String password, int contactNo, String email, String fulfiller, String picture, int requestTotal, int requestNo, int fulfillTotal, int fulfillNo, int requestMade, int fulfillMade) {
        super(id, username, password, contactNo, email, fulfiller, picture);
        this.requestTotal = requestTotal;
        this.requestNo = requestNo;
        this.fulfillTotal = fulfillTotal;
        this.fulfillNo = fulfillNo;
        this.requestMade = requestMade;
        this.fulfillMade = fulfillMade;
    }

    public int getRequestTotal() {
        return requestTotal;
    }

    public void setRequestTotal(int requestTotal) {
        this.requestTotal = requestTotal;
    }

    public int getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(int requestNo) {
        this.requestNo = requestNo;
    }

    public int getFulfillTotal() {
        return fulfillTotal;
    }

    public void setFulfillTotal(int fulfillTotal) {
        this.fulfillTotal = fulfillTotal;
    }

    public int getFulfillNo() {
        return fulfillNo;
    }

    public void setFulfillNo(int fulfillNo) {
        this.fulfillNo = fulfillNo;
    }

    public int getRequestMade() {
        return requestMade;
    }

    public void setRequestMade(int requestMade) {
        this.requestMade = requestMade;
    }

    public int getFulfillMade() {
        return fulfillMade;
    }

    public void setFulfillMade(int fulfillMade) {
        this.fulfillMade = fulfillMade;
    }
}

