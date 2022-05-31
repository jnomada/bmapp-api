package com.bmapp.api.controller;

import com.bmapp.api.HibernateUtil;
import com.bmapp.api.Users;
import com.bmapp.api.ui.model.request.UserLoginRequest;

import java.util.Date;
import javax.persistence.NoResultException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author james
 */
@RestController
@RequestMapping("users")
public class UserController {

    public UserController() {

    }

    
    // Endpoint para la gestión de los login de los usuarios. Comprueba si existe y hace el login, si no existe provaca una excepcion y avisa al usuario 
    // de que esta mal el usuario o la clave
    
    @PostMapping(path = "/userlogin")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {

        try {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("SELECT u FROM Users u WHERE u.username=:username AND u.password=:password");

            query.setParameter("username", userLoginRequest.getUsername());
            query.setParameter("password", userLoginRequest.getPassword());

            Users user = (Users) query.getSingleResult();

            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (NoResultException e) {
            return new ResponseEntity<>("Nombre de usuario o contrseña incorrectos",HttpStatus.BAD_REQUEST);
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    
    // Endpoint para la creación (registro) de nuevos usuarios. Compruba que no exista el usuario y si no exite lo crea. Si existe muestra un mensaje al usuario.
    @PostMapping(consumes = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    }, produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<String> createUser(@RequestBody Users userDetails) {
        
        if(userValidation(userDetails)) {
        
            Session session = null;
            Transaction tx = null;

            try {

                // Crea la sesion y la transacción
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();

                // Se crea un objecto usuario y se llena  con los datos de la petición
                Users user = new Users(userDetails.getUsername(), userDetails.getEmail(), userDetails.getPassword(), new Date());

                // Se persiste el nuevo objeto usuario
                session.save(user);

                // Hacemos la tranaccion difinitiva
                tx.commit();

                // Devolvemos una respuesta al usuario
                return new ResponseEntity<>("Has sido registrado con éxito", HttpStatus.OK);

            } catch (HibernateException e) {

                // Devolvemos una respuesta negativa al usuario
                return new ResponseEntity<>("El usuario ya existe", HttpStatus.BAD_REQUEST);
            
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
            
        } else {
            
            // Si la contraseña no cumple con los requísitos informamos al usuario
            return new ResponseEntity<>("El usuario tiene que tener mínimo 8 characteres, y la contraseña 8 o más", HttpStatus.BAD_REQUEST);
        }


    }
    
    // Funcion para validar el nombre de usuario y la contraseña
    public boolean userValidation(Users user) {
    
        if(user.getUsername().length() >= 6 || user.getPassword().length() >= 8) {
            
            return true;
            
        } else {
            
            return false;
        }
        
    }

    /*
    @PutMapping
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete user was called";
    }
    */
    
    
}
