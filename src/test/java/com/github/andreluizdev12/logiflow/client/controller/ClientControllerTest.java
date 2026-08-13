package com.github.andreluizdev12.logiflow.client.controller;

import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.domain.Client;
import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientAlreadyExistsException;
import com.github.andreluizdev12.logiflow.client.exceptions.ClientNotFoundException;
import com.github.andreluizdev12.logiflow.client.exceptions.DocumentInvalidException;
import com.github.andreluizdev12.logiflow.client.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.client.service.ClientService;
import com.github.andreluizdev12.logiflow.shared.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import({ClientMapper.class, GlobalExceptionHandler.class})
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @Test
    void create_shouldReturnCreatedClient() throws Exception {
        var dto = validCreateDto();
        var client = clientFrom(dto);

        when(clientService.create(any(CreateClientDTO.class))).thenReturn(client);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(client.getId().toString()))
                .andExpect(jsonPath("$.name").value("João da Silva"))
                .andExpect(jsonPath("$.document").value("09532626506"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));

        verify(clientService).create(any(CreateClientDTO.class));
    }

    @Test
    void create_shouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "externalId": "",
                  "sourceSystem": "",
                  "personType": null,
                  "name": "A",
                  "document": "",
                  "telefone": "123456789012345678901",
                  "email": "email-invalido"
                }
                """;

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray());

        verifyNoInteractions(clientService);
    }

    @Test
    void create_shouldReturnConflictWhenDocumentAlreadyExists() throws Exception {
        var dto = validCreateDto();
        when(clientService.create(any(CreateClientDTO.class)))
                .thenThrow(new ClientAlreadyExistsException(dto.document()));

        mockMvc.perform(post("/clients")
                        .header("X-Trace-Id", "trace-controller-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CLIENT_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.traceId").value("trace-controller-test"));
    }

    @Test
    void create_shouldReturnBadRequestWhenDomainRejectsDocument() throws Exception {
        var dto = validCreateDto();
        when(clientService.create(any(CreateClientDTO.class)))
                .thenThrow(new DocumentInvalidException("CPF inválido"));

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DOCUMENT"))
                .andExpect(jsonPath("$.detail").value("CPF inválido"));
    }

    @Test
    void create_shouldReturnBadRequestWhenPersonTypeIsUnknown() throws Exception {
        String invalidBody = """
                {
                  "externalId": "CLI-001",
                  "sourceSystem": "ERP",
                  "personType": "TIPO_INEXISTENTE",
                  "name": "João da Silva",
                  "document": "09532626506",
                  "telefone": "31999998888",
                  "email": "joao@email.com"
                }
                """;

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(clientService);
    }

    @Test
    void getById_shouldReturnClient() throws Exception {
        var client = clientFrom(validCreateDto());
        when(clientService.getById(client.getId())).thenReturn(client);

        mockMvc.perform(get("/clients/{id}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId().toString()))
                .andExpect(jsonPath("$.name").value(client.getName()));

        verify(clientService).getById(client.getId());
    }

    @Test
    void getById_shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        when(clientService.getById(id)).thenThrow(new ClientNotFoundException(id));

        mockMvc.perform(get("/clients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLIENT_NOT_FOUND"));
    }

    @Test
    void getById_shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/clients/{id}", "id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.parameter").value("id"));

        verifyNoInteractions(clientService);
    }

    @Test
    void getAll_shouldReturnClients() throws Exception {
        var firstClient = clientFrom(validCreateDto());
        var secondClient = Client.build(
                "CLI-002",
                "ERP",
                PersonType.PESSOA_FISICA,
                "Maria Silva",
                "529.982.247-25",
                "31988887777",
                "maria@email.com"
        );
        when(clientService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(firstClient, secondClient)));

        mockMvc.perform(get("/clients").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("João da Silva"))
                .andExpect(jsonPath("$[1].name").value("Maria Silva"));

        verify(clientService).getAll(any(Pageable.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        var id = UUID.randomUUID();

        mockMvc.perform(delete("/clients/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(clientService).delete(id);
    }

    @Test
    void delete_shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        doThrow(new ClientNotFoundException(id)).when(clientService).delete(id);

        mockMvc.perform(delete("/clients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLIENT_NOT_FOUND"));
    }

    @Test
    void activate_shouldReturnNoContent() throws Exception {
        var id = UUID.randomUUID();

        mockMvc.perform(patch("/clients/{id}/activate", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(clientService).ativar(id);
    }

    @Test
    void activate_shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        doThrow(new ClientNotFoundException(id)).when(clientService).ativar(id);

        mockMvc.perform(patch("/clients/{id}/activate", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLIENT_NOT_FOUND"));
    }

    @Test
    void update_shouldReturnUpdatedClient() throws Exception {
        var client = clientFrom(validCreateDto());
        var dto = new UpdateClientDTO(
                "João Silva Atualizado",
                "(31) 98888-7777",
                "NOVO@EMAIL.COM"
        );
        client.update(dto.name(), dto.email(), dto.telefone());
        when(clientService.update(eq(client.getId()), any(UpdateClientDTO.class))).thenReturn(client);

        mockMvc.perform(patch("/clients/{id}", client.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.telefone").value("31988887777"))
                .andExpect(jsonPath("$.email").value("novo@email.com"));

        verify(clientService).update(eq(client.getId()), any(UpdateClientDTO.class));
    }

    @Test
    void update_shouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        var id = UUID.randomUUID();
        String invalidBody = """
                {
                  "name": "A",
                  "telefone": "123456789012345678901",
                  "email": "email-invalido"
                }
                """;

        mockMvc.perform(patch("/clients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray());

        verifyNoInteractions(clientService);
    }

    @Test
    void update_shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        var dto = new UpdateClientDTO("Cliente Atualizado", null, null);
        when(clientService.update(eq(id), any(UpdateClientDTO.class)))
                .thenThrow(new ClientNotFoundException(id));

        mockMvc.perform(patch("/clients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLIENT_NOT_FOUND"));
    }

    private CreateClientDTO validCreateDto() {
        return new CreateClientDTO(
                "CLI-001",
                "ERP",
                PersonType.PESSOA_FISICA,
                "João da Silva",
                "095.326.265-06",
                "31999998888",
                "joao@email.com"
        );
    }

    private Client clientFrom(CreateClientDTO dto) {
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
