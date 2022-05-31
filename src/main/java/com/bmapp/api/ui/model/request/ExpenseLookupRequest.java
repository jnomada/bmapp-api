/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmapp.api.ui.model.request;

import java.util.Date;

/**
 *
 * @author james
 */

// Modelo para recibir la petición de los gastos que el usuario quiere ver.
public class ExpenseLookupRequest {
    
    private String username;
    private String password;
    private int expenseYear;
    private int expenseMonth;
    
    public ExpenseLookupRequest() {};

    public ExpenseLookupRequest(String username, String password, int expenseYear, int expenseMonth) {
        this.username = username;
        this.password = password;
        this.expenseYear = expenseYear;
        this.expenseMonth = expenseMonth;
    }

    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExpenseYear() {
        return expenseYear;
    }

    public void setExpenseYear(int expenseYear) {
        this.expenseYear = expenseYear;
    }

    public int getExpenseMonth() {
        return expenseMonth;
    }

    public void setExpenseMonth(int expenseMonth) {
        this.expenseMonth = expenseMonth;
    }

    
    
    
    
    
    
    
    
    
    
}
