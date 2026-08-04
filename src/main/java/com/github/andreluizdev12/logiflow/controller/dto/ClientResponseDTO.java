package com.github.andreluizdev12.logiflow.controller.dto;

import com.github.andreluizdev12.logiflow.domain.client.PersonType;
import com.github.andreluizdev12.logiflow.domain.client.StatusClient;

import java.time.Instant;
import java.util.UUID;

public record ClientResponseDTO(
        UUID id,
        String externalId,
        String  sourceSystem,
        PersonType personType,
        String name,
        String document,
        String telefone,
        String email,
        StatusClient status,
        Instant createdOn,
        Instant updatedOn
) {
}