package com.oazaproject.oaza_project.controller;

import com.oazaproject.oaza_project.exception.OazaException;
import com.oazaproject.oaza_project.model.userModel;
import com.oazaproject.oaza_project.service.UserService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/")

public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    Connection connection;

    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("user")
    public String addUserToDatabase(@RequestBody userModel user) {
        return userService.addUserToDatabase(user);

    }

    @GetMapping("user/{id}")
    public ResponseEntity<userModel> getUserById(
            @PathVariable(value = "id") Long userId,
            @RequestParam(defaultValue = "false", required = false) boolean detail) throws SQLException, OazaException {
        return userService.getUserById(userId, detail);
    }
    @ExceptionHandler(OazaException.class)
    public ResponseEntity<String> handleOazaException(OazaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }



    @GetMapping("users")
    public List<userModel> getAll(
            @RequestParam(defaultValue = "false", required = false) boolean detail)
            throws SQLException {
        return userService.getAll(detail);
    }

    @PutMapping("user")
    public String editUser(@RequestBody userModel user) throws SQLException {
        return userService.editUser(user);
    }

    @DeleteMapping("user/{id}")
    public String deleteUser(@PathVariable(value = "id") Long userId) throws SQLException {
        return userService.deleteUser(userId);

    }
}
