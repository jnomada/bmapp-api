
package com.bmapp.api.ui.model.response;

/**
 *
 * @author james
 */

// 
public class ExpenseType {
    
    private String type;
    private int amount;

    public ExpenseType() {
    }

    public ExpenseType(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
    
    
}
