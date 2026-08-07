package com.github.andreluizdev12.logiflow.client.service;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import com.github.andreluizdev12.logiflow.client.domain.vos.Document;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientAlreadyExistsException;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientNotFoundException;
import com.github.andreluizdev12.logiflow.client.repositorys.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService clientService;

    ArgumentCaptor<Client> clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
   @Test
    public void shouldCreateClientSucessfully () {

        //arrange
        CreateClientDTO dto = new CreateClientDTO(
                "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                "095-326-265.06", "31999998888", "joao.silva@email.com");

        when(repository.existsByDocument(any(Document.class))).thenReturn(false);
        when(repository.save(any(Client.class))).thenAnswer(invocationOnMock ->  invocationOnMock.getArgument(0));
        var saved = clientService.create(dto);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(dto.name(),saved.getName());
        assertEquals(dto.externalId(),saved.getExternalId());
        assertEquals(dto.sourceSystem(),saved.getSourceSystem());
        assertEquals(dto.personType(),saved.getPersonType());

        verify(repository).existsByDocument(Document.of(dto.document()));
        verify(repository).save(any(Client.class));
    }
    @Test
    public void shouldThrowErrorIfDocumentExists () {
        CreateClientDTO dto = new CreateClientDTO(
                "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                "095-326-265.06", "31999998888", "joao.silva@email.com");

        when(repository.existsByDocument(any(Document.class))).thenReturn(true);
       assertThrows(ClientAlreadyExistsException.class, () ->{
          clientService.create(dto);
       });
        verify(repository).existsByDocument(Document.of(dto.document()));

    }


    @Test
    public void shouldThrow () {
        CreateClientDTO dto = new CreateClientDTO(
                "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                "095-326-265.06", "31999998888", "joao.silva@email.com");

        when(repository.existsByDocument(any(Document.class))).thenReturn(true);
        assertThrows(ClientAlreadyExistsException.class, () ->{
            clientService.create(dto);
        });
        verify(repository).existsByDocument(Document.of(dto.document()));

    }


}

