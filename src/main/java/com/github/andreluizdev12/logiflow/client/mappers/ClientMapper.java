package com.github.andreluizdev12.logiflow.client.mappers;

import com.github.andreluizdev12.logiflow.client.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;

import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getExternalId(),
                client.getSourceSystem(),
                client.getPersonType(),
                client.getName(),
                client.getDocument().value(),
                client.getTelefone(),
                client.getEmail().value(),
                client.getStatus(),
                client.getCreatedOn(),
                client.getUpdatedOn()
        );
    }
}
