package com.bmapp.api.ui.model.response;
// Generated 21-jul-2021 17:27:56 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Expenses generated by hbm2java
 */
public class ChartDataResponseModelDaysMonth  implements java.io.Serializable {


     
     private String day;
     private Double amount;
     

    public ChartDataResponseModelDaysMonth() {
    }

    public ChartDataResponseModelDaysMonth(String day, Double amount) {
        this.day = day;
        this.amount = amount;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

   


}


