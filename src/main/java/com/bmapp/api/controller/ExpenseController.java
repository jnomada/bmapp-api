/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmapp.api.controller;

import com.bmapp.api.Expenses;
import com.bmapp.api.HibernateUtil;
import com.bmapp.api.Users;
import com.bmapp.api.ui.model.request.DeleteExpensesModel;
import com.bmapp.api.ui.model.request.ExpenseLookupRequest;
import com.bmapp.api.ui.model.response.ChartDataResponseModel;
import com.bmapp.api.ui.model.response.ChartDataResponseModelDaysMonth;
import com.bmapp.api.ui.model.response.ExpenseType;
import com.bmapp.api.ui.model.response.ExpensesDetailsResponseModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author james
 */
@RestController
@RequestMapping("expenses")
public class ExpenseController {

    //////////////////////////////////////////////////////////////////
    // Endpoint para obtener gastos de la base de datos
    @PostMapping(path = "/getexpense")
    public ResponseEntity<?> getExpenses(@RequestBody ExpenseLookupRequest expenseLookupRequest) {

        Session session = null;
        Transaction tx = null;
        ArrayList<Expenses> expenses = null;
        ArrayList<ExpensesDetailsResponseModel> formattedExpenses = new ArrayList<>();

        try {
            // Se obtiene los datos del usuario para luego utilizarlos para recuperar los datos de lo gastos para este usuario
            Users user = getUser(expenseLookupRequest.getUsername(), expenseLookupRequest.getPassword());
            // Crear la sesión y transacción de Hibernate
            session = HibernateUtil.getSessionFactory().openSession();

            Query query2 = session.createQuery("SELECT e FROM Expenses e WHERE e.userId=:userId AND YEAR(e.expenseDate)=:expenseYear AND MONTH(e.expenseDate)=:expenseMonth ORDER BY e.expenseDate DESC");
            
            query2.setParameter("userId", user.getUserId());
            query2.setParameter("expenseYear", expenseLookupRequest.getExpenseYear());
            query2.setParameter("expenseMonth", expenseLookupRequest.getExpenseMonth());

            expenses = (ArrayList<Expenses>) query2.list();

            // Utilizamos los datos recibidos para rellenar el modelo que se utilizará para dar respuesta al usuario con todos los datos en formato JSON.
            for (int i = 0; i < expenses.size(); i++) {

                formattedExpenses.add(new ExpensesDetailsResponseModel(expenses.get(i).getExpenseId(), expenses.get(i).getExpenseDate().toString(), expenses.get(i).getAmount(), expenses.get(i).getType(), expenses.get(i).getDescription()));

            }
            
            // Enviamos los datos
            return new ResponseEntity<ArrayList>(formattedExpenses, HttpStatus.OK);
            
        } catch (HibernateException e) {

            e.printStackTrace();
            
            return new ResponseEntity<String>("Ha occurido un error", HttpStatus.BAD_REQUEST);
        
        } catch (Exception e) {

            e.printStackTrace();
            
            return new ResponseEntity<String>("Ha occurido un error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Añade un gasto a la base de datos
    @PostMapping(consumes = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    }, produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<String> createExpense(@RequestBody Expenses expenseDetails) {

        Session session = null;
        Transaction tx = null;

        try {
            
            System.out.println(expenseDetails.getUserId());
            System.out.println(expenseDetails.getAmount());
            
            
            // Comprobamos que el usuario haya introducido los datos obligatorios
            if(expenseDetails.getAmount() == null || expenseDetails.getUserId() == null)  return new ResponseEntity<>("El gasto no se ha podido añadir, revisar los datos", HttpStatus.BAD_REQUEST);

            // Creamos sesión y transacción de Hibernate
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Comprobamos que exista y si no asignamos el dia de hoy
            Date fecha = expenseDetails.getExpenseDate();
            if(fecha == null) fecha = new Date(); 
            
            // Crear un nuevo objeto de gasto y lo poblamos con los datos recibidos desde la petición.
            Expenses expense = new Expenses(fecha, expenseDetails.getAmount(), expenseDetails.getUserId(), expenseDetails.getType(), expenseDetails.getDescription());

            // Persistimos el objeto en la base de datos
            session.save(expense);

            // Hacemos que la tranacción sea permanente
            tx.commit();

            // Enviamos la confirmación al usuario
            return new ResponseEntity<>("El gasto ha sido añadido correctamente", HttpStatus.OK);
            
            // Capturamos posibles errores y revertimos (hemos rollback) los cambios. Informamos al usuario.
        } catch (HibernateException e) {

            tx.rollback();
           
            // Return negative response to client
            return new ResponseEntity<>("El gasto no se ha podido añadir, revisar los datos", HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            tx.rollback();
           
            // Return negative response to client
            return new ResponseEntity<>("El gasto no se ha podido añadir, ha ocurrido un error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Elimina un gasto de la base de datos
    @CrossOrigin
    @DeleteMapping(consumes = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    }, produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<String> deleteExpenses(@RequestBody DeleteExpensesModel deleteExpensesModel) {

        Session session = null;
        Transaction tx = null;

        try {

            // Creamos sesión y transacción de Hibernate y seguimos casi los mismo pasos que en las funciones anteriores.
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            List<Integer> expenses = IntStream.of(deleteExpensesModel.getDeleteExpenses()).boxed().collect(Collectors.toList());
            
            List<Expenses> queriedExpensesObjects = new ArrayList<>();

            Query query = session.createQuery("SELECT e FROM Expenses e WHERE e.userId=:userId AND e.expenseId IN(?2)");
            query.setParameter("userId", Integer.parseInt(deleteExpensesModel.getUserId()));
            query.setParameter(2, expenses);
            queriedExpensesObjects = (List<Expenses>) query.list();

            for (int i = 0; i < queriedExpensesObjects.size(); i++) {

                session.delete(queriedExpensesObjects.get(i));
            }

            // Commit cambios
            tx.commit();

            // Responder al cliente
            return new ResponseEntity<>("Expenses have been deleted", HttpStatus.OK);

        } catch (HibernateException e) {

            tx.rollback();
            // Devolver una respuesta negativa al cliente
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            
        } finally {

            session.close();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Comprobar que exista el usuario en la base de datos
    public Users getUser(String username, String password) {

        Session session = null;
        Transaction tx = null;
        Users user = null;

        try {

            // Create Hibernate session and transaction
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("SELECT u FROM Users u WHERE u.username=:username AND u.password=:password");

            query.setParameter("username", username);
            query.setParameter("password", password);

            user = (Users) query.getSingleResult();

        } catch (HibernateException e) {

            e.printStackTrace();
        }

        return user;

    }
    
    //////////////////////////////////////////////////////////////////
    // Metodos para obtener los datos de los gráficos
    
    @PostMapping(path = "/chartdata")
    public ResponseEntity<ArrayList> getChartData(@RequestBody ExpenseLookupRequest expenseLookupRequest) {

        Session session = null;
        Transaction tx = null;
        ArrayList<ChartDataResponseModel> expenses = null;

        try {
            Users user = getUser(expenseLookupRequest.getUsername(), expenseLookupRequest.getPassword());
            // Create Hibernate session and transaction
            session = HibernateUtil.getSessionFactory().openSession();

            Query query2 = session.createQuery("SELECT MONTH(e.expenseDate), SUM(e.amount) FROM Expenses e WHERE e.userId=:userId AND YEAR(e.expenseDate)=:expenseYear GROUP BY MONTH(e.expenseDate)");
            
            query2.setParameter("userId", user.getUserId());
            query2.setParameter("expenseYear", expenseLookupRequest.getExpenseYear());

            expenses = (ArrayList<ChartDataResponseModel>) query2.list();


        } catch (HibernateException e) {

            e.printStackTrace();
        }

        return new ResponseEntity<ArrayList>(expenses, HttpStatus.OK);

    }
    
      
    
    // TIPO DE GASTOS MES
    @PostMapping(path = "/typeMonthData")
    public ResponseEntity<ArrayList> getTypeMonthData(@RequestBody ExpenseLookupRequest expenseLookupRequest) {

        Session session = null;
        Transaction tx = null;
        ArrayList<ChartDataResponseModel> expenses = null;
        ArrayList<ExpenseType> expenseTypes = new ArrayList<>();

        try {
            Users user = getUser(expenseLookupRequest.getUsername(), expenseLookupRequest.getPassword());
            // Create Hibernate session and transaction
            session = HibernateUtil.getSessionFactory().openSession();

            
            Query query3 = session.createQuery("SELECT type, SUM(e.amount) FROM Expenses e WHERE e.userId=:userId AND YEAR(e.expenseDate)=:expenseYear AND MONTH(e.expenseDate)=:expenseMonth GROUP BY e.type");            
            query3.setParameter("userId", user.getUserId());
            query3.setParameter("expenseYear", expenseLookupRequest.getExpenseYear());
            query3.setParameter("expenseMonth", expenseLookupRequest.getExpenseMonth());

            expenseTypes = (ArrayList<ExpenseType>) query3.list();
            
            
        } catch (HibernateException e) {

            e.printStackTrace();
        }

        return new ResponseEntity<ArrayList>(expenseTypes, HttpStatus.OK);

    }
    
    
    // TIPO DE GASTOS AÑO
    @PostMapping(path = "/typeYearData")
    public ResponseEntity<ArrayList> getTypeYearData(@RequestBody ExpenseLookupRequest expenseLookupRequest) {

        Session session = null;
        Transaction tx = null;
        ArrayList<ChartDataResponseModel> expenses = null;
        ArrayList<ExpenseType> expenseTypes = new ArrayList<>();

        try {
            Users user = getUser(expenseLookupRequest.getUsername(), expenseLookupRequest.getPassword());
            // Create Hibernate session and transaction
            session = HibernateUtil.getSessionFactory().openSession();

            
            Query query3 = session.createQuery("SELECT type, SUM(e.amount) FROM Expenses e WHERE e.userId=:userId AND YEAR(e.expenseDate)=:expenseYear GROUP BY e.type");            
            query3.setParameter("userId", user.getUserId());
            query3.setParameter("expenseYear", expenseLookupRequest.getExpenseYear());

            expenseTypes = (ArrayList<ExpenseType>) query3.list();
            
            
        } catch (HibernateException e) {

            e.printStackTrace();
        }

        return new ResponseEntity<ArrayList>(expenseTypes, HttpStatus.OK);

    }
    
    // TIPO DE GASTOS AÑO
    @PostMapping(path = "/daysOfMonthData")
    public ResponseEntity<ArrayList> getDaysOfMonthData(@RequestBody ExpenseLookupRequest expenseLookupRequest) {

        Session session = null;
        Transaction tx = null;
        ArrayList<ChartDataResponseModelDaysMonth> expenses = null;
        

        try {
            Users user = getUser(expenseLookupRequest.getUsername(), expenseLookupRequest.getPassword());
            // Create Hibernate session and transaction
            session = HibernateUtil.getSessionFactory().openSession();

            
            Query query3 = session.createQuery("SELECT DAY(e.expenseDate), SUM(e.amount) FROM Expenses e WHERE e.userId=:userId AND YEAR(e.expenseDate)=:expenseYear AND MONTH(e.expenseDate)=:expenseMonth GROUP BY DAY(e.expenseDate)");            
            query3.setParameter("userId", user.getUserId());
            query3.setParameter("expenseYear", expenseLookupRequest.getExpenseYear());
            query3.setParameter("expenseMonth", expenseLookupRequest.getExpenseMonth());

            expenses = (ArrayList<ChartDataResponseModelDaysMonth>) query3.list();
            
            
        } catch (HibernateException e) {

            e.printStackTrace();
        }

        return new ResponseEntity<ArrayList>(expenses, HttpStatus.OK);

    }

}
