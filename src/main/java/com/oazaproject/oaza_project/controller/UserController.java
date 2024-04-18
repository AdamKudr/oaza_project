package com.oazaproject.oaza_project.controller;

import com.oazaproject.oaza_project.exception.OazaException;
import com.oazaproject.oaza_project.model.userModel;
import com.oazaproject.oaza_project.service.LoadFromFile;
import com.oazaproject.oaza_project.service.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/")

public class UserController {

    @Autowired
    UuidGenerator generate;

    @Autowired
    LoadFromFile load;

    @PostMapping("user")
    public String addUserToDatabase(@RequestBody userModel user) {

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");

            String preparedSQL = "INSERT INTO oaza_users (name, surname, personid, uuid) VALUES (?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(preparedSQL);

            load.loadPersonIDFromFile("dataPersonID.txt");

            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getPersonId());
            statement.setString(4, generate.generateUuid().toString());

            if (!load.getPersonIDList().contains(user.getPersonId())) {
                return "Invalid PersonID. Please enter valid PersonID";
            } else {
                int rowsAffected = statement.executeUpdate();

                statement.close();
                connection.close();

                if (rowsAffected > 0) {
                    return "User added successfully";
                } else {
                    return "Failed to add user";
                }
            }

        } catch (SQLException | OazaException e) {
            e.printStackTrace();
            return "Unable to write entry into the database. " + e.getMessage();
        }
    }

    @GetMapping("user/{id}")
    public ResponseEntity<userModel> getUserById(
            @PathVariable(value = "id") Long userId,
            @RequestParam(defaultValue = "false", required = false) boolean detail) throws SQLException, OazaException {

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");
        String preparedSQL = "SELECT * FROM oaza_users WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(preparedSQL);
        statement.setLong(1, userId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String personId = detail ? resultSet.getString("personid") : "";
            String uuid = detail ? resultSet.getString("uuid") : "";
            userModel userModel = new userModel(id, name, surname, personId, uuid);

            resultSet.close();
            statement.close();
            connection.close();

            return ResponseEntity.ok(userModel);
        } else {
            throw new OazaException("User with id '" + userId + "' not found in the database");
        }

    }

    @ExceptionHandler(OazaException.class)
    public ResponseEntity<String> handleOazaException(OazaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


    @GetMapping("users")
    public List<userModel> getAll(
            @RequestParam(defaultValue = "false", required = false) boolean detail)
            throws SQLException {

        List<userModel> usersList = new ArrayList<>();

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");

        Statement statement = connection.createStatement();

        String query = "SELECT * FROM oaza_users";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String personId = detail ? resultSet.getString("personid") : "";
            String uuid = detail ? resultSet.getString("uuid") : "";
            userModel userModel = new userModel(id, name, surname, personId, uuid);

            usersList.add(userModel);
        }
        resultSet.close();
        statement.close();
        connection.close();

        return usersList;
    }

    @PutMapping("user")
    public String editUser(@RequestBody userModel user) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");

        String preparedSQL = "UPDATE oaza_users SET name = ?, surname = ? WHERE id = ?";

        PreparedStatement statement = connection.prepareStatement(preparedSQL);

        statement.setString(1, user.getName());
        statement.setString(2, user.getSurname());
        statement.setString(3, user.getId());

        int rowsAffected = statement.executeUpdate();

        statement.close();
        connection.close();

        if (rowsAffected > 0) {
            return "User updated successfully";
        } else {
            return "Failed to update user";
        }
    }

    @DeleteMapping("user/{id}")
    public String deleteUser(@PathVariable(value = "id") Long userId) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oaza_project?user=root&password=Adam_33323");
        String preparedSQL = "DELETE FROM oaza_users WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(preparedSQL);
        statement.setLong(1, userId);
        int rowsAffected = statement.executeUpdate();

        statement.close();
        connection.close();

        if (rowsAffected > 0) {
            return "User deleted successfully";
        } else {
            return "Failed to delete user";
        }
    }

}
