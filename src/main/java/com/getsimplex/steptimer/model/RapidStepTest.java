//© 2021 Sean Murdock

package com.getsimplex.steptimer.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 10/17/2016.
 */
public class RapidStepTest implements Comparable<RapidStepTest>{
    private String token;
    private String userName;
    private String customerPhone;
    private String customerBirthDate;
    private Long startTime;
    private ArrayList<Long> stepPoints;
    private Long stopTime;
    private Long testTime;
    private Integer totalSteps;
    private Integer feelingScore;
    private String customer;

    @Override
    public int compareTo(RapidStepTest rapidStepTest){
        return this.stopTime.compareTo(rapidStepTest.stopTime);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public ArrayList getStepPoints() {
        return stepPoints;
    }


    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public Long getTestTime() {
        return testTime;
    }

    public void setTestTime(Long testTime) {
        this.testTime = testTime;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public Integer getFeelingScore() {
        return feelingScore;
    }

    public void setFeelingScore(Integer feelingScore) {
        this.feelingScore = feelingScore;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerBirthDate() {
        return customerBirthDate;
    }

    public void setCustomerBirthDate(String customerBirthDate) {
        this.customerBirthDate = customerBirthDate;
    }

    public void setStepPoints(ArrayList<Long> stepPoints) {
        this.stepPoints = stepPoints;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
