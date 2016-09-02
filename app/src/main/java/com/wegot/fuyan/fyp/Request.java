package com.wegot.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 7/11/2016.
 */
public class Request implements Serializable{

    int id;
    int requestorId;
    int imageResource;
    String productName;
    String requirement;
    String location;
    int postal;
    String startTime, endTime;
    int duration;
    double price;
    String status, requestImage;

    public Request(int imageResource, String productName, String requirement){
        this.imageResource = imageResource;
        this.productName = productName;
        this.requirement = requirement;
    }

    public Request(int id, int requestorId, int imageResource, String productName, String requirement, String location, int postal, String startTime,
    String endTime, int duration, double price, String status){

        this.id = id;
        this.requestorId = requestorId;
        this.imageResource = imageResource;
        this.productName = productName;
        this.requirement = requirement;
        this.location = location;
        this.postal = postal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.price = price;
        this.status = status;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(int requestorId) {
        this.requestorId = requestorId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPostal() {
        return postal;
    }

    public void setPostal(int postal) {
        this.postal = postal;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getRequestImage() {
        return requestImage;
    }

    public void setRequestImage(String requestImage) {
        this.requestImage = requestImage;
    }
}
