package com.oazaproject.oaza_project.service;

import com.oazaproject.oaza_project.exception.OazaException;
import com.oazaproject.oaza_project.model.userModel;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

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

    @Autowired
    UuidGenerator generate;

    @Autowired
    LoadFromFile load;

    public String addUserToDatabase(@RequestBody userModel user) {

        try {

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

    public ResponseEntity<userModel> getUserById(
            @PathVariable(value = "id") Long userId,
            @RequestParam(defaultValue = "false", required = false) boolean detail) throws SQLException, OazaException {

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

            return ResponseEntity.ok(userModel);
        } else {
            throw new OazaException("User with id '" + userId + "' not found in the database");
        }

    }

    public List<userModel> getAll(
            @RequestParam(defaultValue = "false", required = false) boolean detail)
            throws SQLException {

        List<userModel> usersList = new ArrayList<>();
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

        return usersList;
    }

    public String editUser(@RequestBody userModel user) throws SQLException {
        String preparedSQL = "UPDATE oaza_users SET name = ?, surname = ? WHERE id = ?";

        PreparedStatement statement = connection.prepareStatement(preparedSQL);

        statement.setString(1, user.getName());
        statement.setString(2, user.getSurname());
        statement.setString(3, user.getId());

        int rowsAffected = statement.executeUpdate();

        statement.close();

        if (rowsAffected > 0) {
            return "User updated successfully";
        } else {
            return "Failed to update user";
        }
    }

    public String deleteUser(@PathVariable(value = "id") Long userId) throws SQLException {
        String preparedSQL = "DELETE FROM oaza_users WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(preparedSQL);
        statement.setLong(1, userId);
        int rowsAffected = statement.executeUpdate();

        statement.close();

        if (rowsAffected > 0) {
            return "User deleted successfully";
        } else {
            return "Failed to delete user";
        }
    }

}
