
package com.bmapp.api.ui.model.request;

import java.time.LocalDate;

/**
 *
 * @author james
 */

// Modelo para recoger los datos de registro de un nuevo usuario
public class UserDetailsRequestModel {
    
    private String userId;
    private String email;
    private String password;
    private LocalDate startDate;
    
    public UserDetailsRequestModel() {};
    
    public UserDetailsRequestModel(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    
}
