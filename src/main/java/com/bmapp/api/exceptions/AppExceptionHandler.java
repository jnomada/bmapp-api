
package com.bmapp.api.exceptions;

import com.bmapp.api.ui.model.response.ErrorMessage;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author james
 */

@ControllerAdvice
public class AppExceptionHandler {
    
    
    // Devuelve una excepción personalizado en luger del genérico para Exception    
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request) {
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), "Ha habido un error");
        
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
}
