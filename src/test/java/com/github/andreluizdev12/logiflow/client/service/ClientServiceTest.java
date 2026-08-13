package com.github.andreluizdev12.logiflow.client.service;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import com.github.andreluizdev12.logiflow.client.domain.enums.StatusClient;
import com.github.andreluizdev12.logiflow.client.domain.vos.Document;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientAlreadyExistsException;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientNotFoundException;
import com.github.andreluizdev12.logiflow.client.exceptions.DocumentInvalidException;
import com.github.andreluizdev12.logiflow.client.exceptions.InvalidDocumentForPersonTypeException;
import com.github.andreluizdev12.logiflow.client.repositorys.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void create_shouldCreateClientSuccessfully() {
        var dto = validPhysicalPersonDto();

        when(repository.existsByDocument(any(Document.class))).thenReturn(false);
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var saved = clientService.create(dto);

        assertAll(
                () -> assertNotNull(saved),
                () -> assertNotNull(saved.getId()),
                () -> assertEquals(dto.name(), saved.getName()),
                () -> assertEquals(dto.externalId(), saved.getExternalId()),
                () -> assertEquals(dto.sourceSystem(), saved.getSourceSystem()),
                () -> assertEquals(dto.personType(), saved.getPersonType()),
                () -> assertEquals(StatusClient.ATIVO, saved.getStatus()),
                () -> assertNotNull(saved.getCreatedOn())
        );

        verify(repository).existsByDocument(Document.of(dto.document()));
        verify(repository).save(any(Client.class));
    }

    @Test
    void create_shouldCreateClientWithActiveStatus() {
        var dto = validPhysicalPersonDto();

        when(repository.existsByDocument(any(Document.class))).thenReturn(false);
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = clientService.create(dto);

        assertEquals(StatusClient.ATIVO, result.getStatus());
        verify(repository).save(any(Client.class));
    }

    @Test
    void create_shouldCreateLegalPersonWithValidCnpj() {
        var dto = new CreateClientDTO(
                "CLI-002",
                "ERP",
                PersonType.PESSOA_JURIDICA,
                "Empresa Exemplo Ltda",
                "31.559.589/0001-96",
                "3133334444",
                "contato@empresa.com"
        );

        when(repository.existsByDocument(any(Document.class))).thenReturn(false);
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = clientService.create(dto);

        assertAll(
                () -> assertEquals(PersonType.PESSOA_JURIDICA, result.getPersonType()),
                () -> assertEquals("31559589000196", result.getDocument().value()),
                () -> assertEquals(StatusClient.ATIVO, result.getStatus())
        );

        verify(repository).save(any(Client.class));
    }

    @Test
    void create_shouldThrowErrorIfDocumentExists() {
        var dto = validPhysicalPersonDto();

        when(repository.existsByDocument(any(Document.class))).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () -> clientService.create(dto));

        verify(repository).existsByDocument(Document.of(dto.document()));
        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void create_shouldNotSaveClientWhenDocumentIsInvalid() {
        var dto = new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "012.326.265-06",
                "31999998888",
                "joao.silva@email.com"
        );

        assertThrows(DocumentInvalidException.class, () -> clientService.create(dto));

        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowExceptionWhenPhysicalPersonHasCnpj() {
        var dto = new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "31.559.589/0001-96",
                "31999998888",
                "joao.silva@email.com"
        );

        assertThrows(InvalidDocumentForPersonTypeException.class, () -> clientService.create(dto));

        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void create_shouldThrowExceptionWhenLegalPersonHasCpf() {
        var dto = new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_JURIDICA,
                "Empresa Exemplo Ltda",
                "095.326.265-06",
                "31999998888",
                "contato@empresa.com"
        );

        assertThrows(InvalidDocumentForPersonTypeException.class, () -> clientService.create(dto));

        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void create_shouldNormalizeClientDataBeforeSaving() {
        var dto = new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "(31) 99999-8888",
                "JOAO.SILVA@EMAIL.COM"
        );

        when(repository.existsByDocument(any(Document.class))).thenReturn(false);

        clientService.create(dto);

        var captor = ArgumentCaptor.forClass(Client.class);
        verify(repository).save(captor.capture());
        var clientToSave = captor.getValue();

        assertAll(
                () -> assertEquals("09532626506", clientToSave.getDocument().value()),
                () -> assertEquals("31999998888", clientToSave.getTelefone()),
                () -> assertEquals("joao.silva@email.com", clientToSave.getEmail().value())
        );
    }

    @Test
    void getById_shouldReturnClientWhenIdExists() {
        var client = validPhysicalClient();
        var id = client.getId();

        when(repository.findById(id)).thenReturn(Optional.of(client));

        var result = clientService.getById(id);

        assertSame(client, result);
        verify(repository).findById(id);
    }

    @Test
    void getById_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist() {
        var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getById(id));

        verify(repository).findById(id);
    }

    @Test
    void getAll_shouldReturnClientsSuccessfully() {
        var pageable = PageRequest.of(0, 10);
        var client1 = validPhysicalClient();
        var client2 = Client.build(
                "CLI-002",
                "ERP",
                PersonType.PESSOA_FISICA,
                "Maria Silva",
                "529.982.247-25",
                "31988887777",
                "maria@email.com"
        );
        var page = new PageImpl<>(List.of(client1, client2), pageable, 2);

        when(repository.findAll(pageable)).thenReturn(page);

        var result = clientService.getAll(pageable);

        assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(List.of(client1, client2), result.getContent())
        );
        verify(repository).findAll(pageable);
    }

    @Test
    void delete_shouldInactivateClientSuccessfully() {
        var client = validPhysicalClient();
        var id = client.getId();

        when(repository.findById(id)).thenReturn(Optional.of(client));

        clientService.delete(id);

        assertEquals(StatusClient.INATIVO, client.getStatus());
        verify(repository).findById(id);
        verify(repository).save(client);
    }

    @Test
    void delete_shouldKeepInactiveClientAvailableForQuery() {
        var client = validPhysicalClient();
        var id = client.getId();

        when(repository.findById(id)).thenReturn(Optional.of(client));

        clientService.delete(id);
        var result = clientService.getById(id);

        assertAll(
                () -> assertSame(client, result),
                () -> assertEquals(StatusClient.INATIVO, result.getStatus())
        );
        verify(repository, times(2)).findById(id);
        verify(repository).save(client);
    }

    @Test
    void delete_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist() {
        var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.delete(id));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void activate_shouldActivateClientSuccessfully() {
        var client = validPhysicalClient();
        var id = client.getId();
        client.desativar();

        when(repository.findById(id)).thenReturn(Optional.of(client));

        clientService.ativar(id);

        assertEquals(StatusClient.ATIVO, client.getStatus());
        verify(repository).findById(id);
        verify(repository).save(client);
    }

    @Test
    void activate_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist() {
        var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.ativar(id));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void update_shouldUpdateClientSuccessfully() {
        var client = validPhysicalClient();
        var id = client.getId();
        var dto = new UpdateClientDTO(
                "João Silva",
                "3123298888",
                "joao.silva@email.com"
        );

        when(repository.findById(id)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = clientService.update(id, dto);

        assertAll(
                () -> assertEquals("João Silva", result.getName()),
                () -> assertEquals("3123298888", result.getTelefone()),
                () -> assertEquals("joao.silva@email.com", result.getEmail().value())
        );
        verify(repository).findById(id);
        verify(repository).save(client);
    }

    @Test
    void update_shouldNormalizePhoneAndEmail() {
        var client = validPhysicalClient();
        var id = client.getId();
        var dto = new UpdateClientDTO(
                null,
                "(31) 98888-7777",
                "NOVO.EMAIL@EXEMPLO.COM"
        );

        when(repository.findById(id)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = clientService.update(id, dto);

        assertAll(
                () -> assertEquals("31988887777", result.getTelefone()),
                () -> assertEquals("novo.email@exemplo.com", result.getEmail().value())
        );
    }

    @Test
    void update_shouldNotChangeImmutableFields() {
        var client = validPhysicalClient();
        var originalId = client.getId();
        var originalExternalId = client.getExternalId();
        var originalSourceSystem = client.getSourceSystem();
        var originalPersonType = client.getPersonType();
        var originalDocument = client.getDocument();
        var originalCreatedOn = client.getCreatedOn();
        var dto = new UpdateClientDTO(
                "João Silva Atualizado",
                "31988887777",
                "atualizado@email.com"
        );

        when(repository.findById(originalId)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = clientService.update(originalId, dto);

        assertAll(
                () -> assertEquals(originalId, result.getId()),
                () -> assertEquals(originalExternalId, result.getExternalId()),
                () -> assertEquals(originalSourceSystem, result.getSourceSystem()),
                () -> assertEquals(originalPersonType, result.getPersonType()),
                () -> assertEquals(originalDocument, result.getDocument()),
                () -> assertEquals(originalCreatedOn, result.getCreatedOn())
        );
    }

    @Test
    void update_shouldThrowClientNotFoundExceptionWhenIdDoesNotExist() {
        var id = UUID.randomUUID();
        var dto = new UpdateClientDTO(
                "João Silva",
                "3123298888",
                "joao.silva@email.com"
        );

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.update(id, dto));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Client.class));
    }

    private CreateClientDTO validPhysicalPersonDto() {
        return new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "31999998888",
                "joao.silva@email.com"
        );
    }

    private Client validPhysicalClient() {
        var dto = validPhysicalPersonDto();
        return Client.build(
                dto.externalId(),
                dto.sourceSystem(),
                dto.personType(),
                dto.name(),
                dto.document(),
                dto.telefone(),
                dto.email()
        );
    }
}
