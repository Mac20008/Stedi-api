//© 2021 Sean Murdock

package com.getsimplex.steptimer.model;

/**
 * Created by sean on 9/7/2016.
 */
public class Customer {

    private String customerName;
    private String email;
    private String phone;
    private String birthDay;

    private String gender;
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender(){ return gender; }
    public void setGender(String gender) {
        this.gender = gender;
    }

}
