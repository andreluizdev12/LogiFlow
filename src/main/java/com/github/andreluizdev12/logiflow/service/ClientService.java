package com.github.andreluizdev12.logiflow.service;

import com.github.andreluizdev12.logiflow.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.domain.client.Client;
import com.github.andreluizdev12.logiflow.domain.client.vos.Document;
import com.github.andreluizdev12.logiflow.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.repositorys.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.util.UUID;
@Service
public class ClientService {
    public ClientService(ClientMapper mapper, ClientRepository clientRepository) {
        this.mapper = mapper;
        this.repository = clientRepository;
    }
    private  final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientMapper mapper;
    private  final ClientRepository repository;

    @Transactional
    public Client create (CreateClientDTO dto){
        if(repository.existsByDocument(Document.of(dto.document()))){
            throw  new RuntimeException("A client with this document already exists");
        }
        Client newClient = Client.build(dto.externalId(),dto.sourceSystem(),dto.personType(), dto.name(), dto.document(), dto.telefone(), dto.email());
        logger.info("User with id: " + newClient + " created");
        return  repository.save(newClient);


    }


}
