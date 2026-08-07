package com.github.andreluizdev12.logiflow.client.service;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.domain.vos.Document;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientAlreadyExistsException;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientNotFoundException;
import com.github.andreluizdev12.logiflow.client.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.client.repositorys.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw  new ClientAlreadyExistsException(dto.document());
        }
        Client newClient = Client.build(dto.externalId(),dto.sourceSystem(),dto.personType(), dto.name(), dto.document(), dto.telefone(), dto.email());
        logger.info("User with id: " + newClient + " created");
        return  repository.save(newClient);

    }

    @Transactional(readOnly = true)
    public Client getById (UUID id){
        logger.info("Searching for client with id: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public Page<Client> getAll (Pageable pageable){
        logger.info("Searching all clients");
        return repository.findAll(pageable);
    }

    @Transactional
    public void delete (UUID id){
        logger.info("Deleting client with id: " + id);
        var user = getById(id);
        user.desativar();
        repository.save(user);
    }


    @Transactional
    public void ativar (UUID id){
        logger.info("Activating client with id: " + id);
        var user = getById(id);
        user.ativar();
        repository.save(user);
    }

    @Transactional
    public Client update (UUID id, UpdateClientDTO dto){
        logger.info("UPDATING client with id: " + id);
        var user = getById(id);
        user.update(dto.name(), dto.email(), dto.telefone());
        return  repository.save(user);
    }










}
