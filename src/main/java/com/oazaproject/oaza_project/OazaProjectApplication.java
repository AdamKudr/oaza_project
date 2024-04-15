package com.oazaproject.oaza_project;

import com.oazaproject.oaza_project.exception.OazaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class OazaProjectApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(OazaProjectApplication.class, args);
    }

}
