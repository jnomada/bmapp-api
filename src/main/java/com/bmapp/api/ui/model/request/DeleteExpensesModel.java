
package com.bmapp.api.ui.model.request;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author james
 */

// Modelo para recibir los datos enviados desde el cliente en formato JSON para la eliminación de gastos
public class DeleteExpensesModel implements Serializable {
    
    private String userId;
    private int[] deleteExpenses;
    
    public DeleteExpensesModel() {};
    
    public DeleteExpensesModel(String userId, int[] deleteExpenses) {
        this.userId = userId;
        this.deleteExpenses = deleteExpenses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int[] getDeleteExpenses() {
        return deleteExpenses;
    }

    public void setDeleteExpenses(int[] deleteExpenses) {
        this.deleteExpenses = deleteExpenses;
    }

    
}