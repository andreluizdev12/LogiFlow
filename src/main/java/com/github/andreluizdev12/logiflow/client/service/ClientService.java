package com.github.andreluizdev12.logiflow.client.service;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientAlreadyExistsException;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientNotFoundException;
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

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository repository;

    public ClientService(ClientRepository clientRepository) {
        this.repository = clientRepository;
    }

    @Transactional
    public Client create(CreateClientDTO dto) {
        logger.info("Creating client from sourceSystem={} externalId={}", dto.sourceSystem(), dto.externalId());

        Client newClient = Client.build(
                dto.externalId(),
                dto.sourceSystem(),
                dto.personType(),
                dto.name(),
                dto.document(),
                dto.telefone(),
                dto.email()
        );

        if (repository.existsByDocument(newClient.getDocument())) {
            throw new ClientAlreadyExistsException(dto.document());
        }

        Client savedClient = repository.save(newClient);
        logger.info("Client created with id={}", newClient.getId());
        return savedClient;
    }

    @Transactional(readOnly = true)
    public Client getById(UUID id) {
        logger.info("Searching for client with id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Client> getAll(Pageable pageable) {
        logger.info("Searching clients page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Inactivating client with id={}", id);
        var client = getById(id);
        client.desativar();
        repository.save(client);
    }

    @Transactional
    public void ativar(UUID id) {
        logger.info("Activating client with id={}", id);
        var client = getById(id);
        client.ativar();
        repository.save(client);
    }

    @Transactional
    public Client update(UUID id, UpdateClientDTO dto) {
        logger.info("Updating client with id={}", id);
        var client = getById(id);
        client.update(dto.name(), dto.email(), dto.telefone());
        return repository.save(client);
    }
}
