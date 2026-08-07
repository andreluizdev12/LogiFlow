package com.github.andreluizdev12.logiflow.client.mappers;

import com.github.andreluizdev12.logiflow.client.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;

import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(CreateClientDTO request) {
        Client client = new Client();

        client.setExternalId(request.externalId());
        client.setSourceSystem(request.sourceSystem());
        client.setPersonType(request.personType());
        client.setName(request.name());
//        client.setDocument(request.document());
        client.changePhone(request.telefone());
        client.changeEmail(request.email());

        return client;
    }

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