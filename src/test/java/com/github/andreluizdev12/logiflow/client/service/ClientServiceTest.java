package com.github.andreluizdev12.logiflow.client.service;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import com.github.andreluizdev12.logiflow.client.domain.enums.StatusClient;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        public void create_shouldCreateClientSuccessfully () {
            //arrange
            CreateClientDTO dto = new CreateClientDTO(
                    "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                    "095-326-265.06", "31999998888", "joao.silva@email.com");

            when(repository.existsByDocument(any(Document.class))).thenReturn(false);
            when(repository.save(any(Client.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
            var saved = clientService.create(dto);
            assertNotNull(saved);
            assertNotNull(saved.getId());
            assertEquals(dto.name(), saved.getName());
            assertEquals(dto.externalId(), saved.getExternalId());
            assertEquals(dto.sourceSystem(), saved.getSourceSystem());
            assertEquals(dto.personType(), saved.getPersonType());

            verify(repository).existsByDocument(Document.of(dto.document()));
            verify(repository).save(any(Client.class));
        }
        @Test
        public void create_shouldThrowErrorIfDocumentExists () {
            CreateClientDTO dto = new CreateClientDTO(
                    "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                    "095-326-265.06", "31999998888", "joao.silva@email.com");

            when(repository.existsByDocument(any(Document.class))).thenReturn(true);
            assertThrows(ClientAlreadyExistsException.class, () -> {
                clientService.create(dto);
            });

            verify(repository).existsByDocument(Document.of(dto.document()));

            verify(repository).existsByDocument(Document.of(dto.document()));

        }


        @Test
      void create_shouldNotSaveClientWhenDocumentIsInvalid () {
            CreateClientDTO dto = new CreateClientDTO(
                    "CLI-001", "ERP", PersonType.PESSOA_FISICA, "João da Silva",
                    "095-326-265.06", "31999998888", "joao.silva@email.com");

            when(repository.existsByDocument(any(Document.class))).thenReturn(true);
            assertThrows(ClientAlreadyExistsException.class, () -> {
                clientService.create(dto);
            });
            verify(repository).existsByDocument(Document.of(dto.document()));
        }
            @Test
            void create_shouldNormalizeClientDataBeforeSaving () {
                var dto = new CreateClientDTO(
                        "CLI-001",
                        "ERP",
                        PersonType.PESSOA_FISICA,
                        "João da Silva",
                        "095.326.265-06",
                        "(31) 99999-8888",
                        "JOAO.SILVA@EMAIL.COM"
                );

                when(repository.existsByDocument(any(Document.class)))
                        .thenReturn(false);

                clientService.create(dto);

                var captor = ArgumentCaptor.forClass(Client.class);

                verify(repository).save(captor.capture());

                var clientToSave = captor.getValue();

                assertAll(
                        () -> assertEquals("09532626506", clientToSave.getDocument().value()),
                        () -> assertEquals("31999998888", clientToSave.getTelefone()),
                        () -> assertEquals("joao.silva@email.com", clientToSave.getEmail().value())
                );

                verify(repository)
                        .existsByDocument(Document.of(dto.document()));
            }


            @Test
            void getByID_shouldReturnClientWhenIdExists () {
                Client client = Client.build(
                        "CLI-001",
                        "ERP",
                        PersonType.PESSOA_FISICA,
                        "João da Silva",
                        "095.326.265-06",
                        "(31) 99999-8888",
                        "JOAO.SILVA@EMAIL.COM"
                );

                var id = client.getId();

                when(repository.findById(id))
                        .thenReturn(Optional.of(client));

                var result = clientService.getById(id);

                assertSame(client, result);

                verify(repository).findById(id);
            }
            @Test
            void getByID_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist () {
                var id = UUID.randomUUID();

                when(repository.findById(id))
                        .thenReturn(Optional.empty());

                assertThrows(ClientNotFoundException.class, () -> clientService.getById(id));

                verify(repository).findById(id);
            }
            @Test
            public void getAll_shouldReturnListClientsSuccessfully () {
                var pageable = PageRequest.of(0, 10);
                var client1 = Client.build(
                        "CLI-001",
                        "ERP",
                        PersonType.PESSOA_FISICA,
                        "João da Silva",
                        "095.326.265-06",
                        "(31) 99999-8888",
                        "joao@email.com"
                );

                var client2 = Client.build(
                        "CLI-002",
                        "ERP",
                        PersonType.PESSOA_FISICA,
                        "Maria Silva",
                        "095.326.265-06",
                        "(31) 98888-7777",
                        "maria@email.com"
                );

                Page<Client> page = new PageImpl<>(
                        List.of(client1, client2),
                        pageable,
                        2
                );

                when(repository.findAll(pageable))
                        .thenReturn(page);

                var result = clientService.getAll(pageable);
                assertEquals(2, result.getTotalElements());

                verify(repository).findAll(pageable);
            }

            @Test
            void delete_shouldDeletClientSuccessfully () {
                var client = Client.build(
                        "CLI-001",
                        "ERP",
                        PersonType.PESSOA_FISICA,
                        "João da Silva",
                        "095.326.265-06",
                        "(31) 99999-8888",
                        "joao@email.com"
                );

                var id = client.getId();

                when(repository.findById(id))
                        .thenReturn(Optional.of(client));

                clientService.delete(id);

                assertEquals(StatusClient.INATIVO, client.getStatus());


                verify(repository).findById(id);
                verify(repository).save(client);

            }
    @Test
    public void delete_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist () {
        var client = Client.build(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "joao@email.com"
        );

        var id = client.getId();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.delete(id));

        verify(repository).findById(id);
    }

    @Test
    void ativar_shouldDeletClientSuccessfully () {
        var client = Client.build(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "joao@email.com"
        );

        var id = client.getId();

        when(repository.findById(id))
                .thenReturn(Optional.of(client));

        clientService.ativar(id);

        assertEquals(StatusClient.ATIVO, client.getStatus());


        verify(repository).findById(id);
        verify(repository).save(client);

    }

    @Test
    public void ativar_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist () {
        var client = Client.build(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "joao@email.com"
        );

        var id = client.getId();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.ativar(id));

        verify(repository).findById(id);
    }

    @Test
    public void update_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist () {
        var client = Client.build(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "joao@email.com"
        );


        var dto =  new UpdateClientDTO(
                "João Silva",
                "(31) 2329-8888",
                "joao.silva@email.com"
        );
        var id = client.getId();

        when(repository.findById(id))
                .thenReturn(Optional.of(client));
        when(repository.save(any(Client.class)))
                .thenReturn(client);

        var result = clientService.update(id,dto);


        assertAll(
                () -> assertEquals("João Silva", result.getName()),
                () -> assertEquals("3123298888", result.getTelefone()),
                () -> assertEquals("joao.silva@email.com", result.getEmail().value())
        );

        verify(repository).findById(id);
        verify(repository).save(client);
    }

    @Test
    public void updtate_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist () {
        var client = Client.build(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "joao@email.com"
        );

        var dto =  new UpdateClientDTO(
                "João Silva",
                "(31) 2329-8888",
                "joao.silva@email.com"
        );
        var id = client.getId();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.update(id,dto));

        verify(repository).findById(id);
    }
}






