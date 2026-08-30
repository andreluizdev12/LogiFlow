package com.github.andreluizdev12.logiflow.client.controller;


import com.github.andreluizdev12.logiflow.client.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.client.service.ClientService;
import com.github.andreluizdev12.logiflow.shared.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
@Tag(
        name = "Clientes",
        description = "Gerenciamento de clientes"
)
public class ClientController {
    private final ClientService clientService;
    private final ClientMapper mapper;

    public ClientController(ClientService clientService, ClientMapper mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado")
    })
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody CreateClientDTO createClientDTO) {
        var cliente = clientService.create(createClientDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(cliente));
    }

    @Operation(summary = "Buscar cliente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable UUID id) {
        var cliente = clientService.getById(id);
        return ResponseEntity.ok(mapper.toResponse(cliente));
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<PageResponseDTO<ClientResponseDTO>> getAll(
            @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable
    ) {
        Page<ClientResponseDTO> clientes = clientService.getAll(pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(PageResponseDTO.from(clientes));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente inativado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Reativar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente reativado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        clientService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar dados editáveis do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateClientDTO dto
    ) {
        var client = clientService.update(id, dto);
        return ResponseEntity.ok(mapper.toResponse(client));
    }
}
