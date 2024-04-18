package com.oazaproject.oaza_project.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidGenerator {

    public UUID generateUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid;
    }
}
