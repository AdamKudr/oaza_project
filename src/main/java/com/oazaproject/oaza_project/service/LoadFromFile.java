package com.oazaproject.oaza_project.service;

import com.oazaproject.oaza_project.exception.OazaException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service

public class LoadFromFile {

    List<String> selectedPersonID = new ArrayList<>();
    List<String> personIDList = new ArrayList<>();

    public List<String> getPersonIDList() {
        return personIDList;
    }

    public void loadPersonIDFromFile(List<String> personID) {
        this.personIDList = personID;
    }

    public List<String> getSelectedPersonID() {
        return selectedPersonID;
    }

    public void setSelectedPersonID(List<String> selectedPersonID) {
        this.selectedPersonID = selectedPersonID;
    }

    public List<String> loadPersonIDFromFile(String fileName) throws OazaException {

        try (Scanner scanner = new Scanner(getClass().getResourceAsStream("/" + fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                personIDList.add(line);
            }
            return personIDList;
        } catch (NullPointerException e) {
            throw new OazaException("Soubor nenalezen");
        }

    }

}
