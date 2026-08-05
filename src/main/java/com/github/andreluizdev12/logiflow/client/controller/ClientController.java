package com.github.andreluizdev12.logiflow.client.controller;


import com.github.andreluizdev12.logiflow.client.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.client.controller.dto.UpdateClientDTO;
import com.github.andreluizdev12.logiflow.client.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@Tag(
        name = "Clientes",
        description = "Gerenciamento de clientes"
)
public class ClientController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper mapper;


    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientResponseDTO> create (@Valid @RequestBody CreateClientDTO createClientDTO) {
      var cliente =  clientService.create(createClientDTO);
        return ResponseEntity.ok(mapper.toResponse(cliente));

    }
    @Operation(summary = "Buscar cliente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById (@PathVariable UUID id) {
        var cliente =  clientService.getById(id);
        return ResponseEntity.ok(mapper.toResponse(cliente));

    }
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAll ( @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable){
        var clientes =  clientService.getAll(pageable);
        return ResponseEntity.ok(clientes.stream().map(c ->{
           return mapper.toResponse(c);
        }).toList());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete (@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();

    }


    @GetMapping("/ativar/{id}")
    public ResponseEntity ativar (@PathVariable UUID id) {
        clientService.ativar(id);
        return ResponseEntity.noContent().build();

    }
    @PatchMapping("/{id}")
    public ResponseEntity update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateClientDTO dto
    ) {
        var client =clientService.update(id, dto);
        return ResponseEntity.ok(mapper.toResponse(client));
    }

//    @PostMapping("/add")
//    public ResponseEntity<List<ClientResponseDTO>> createAll(
//             @RequestBody List< @Valid CreateClientDTO> clientsDTO) {
//
//        List<ClientResponseDTO> response = clientsDTO.stream()
//                .map(clientService::create)
//                .map(mapper::toResponse)
//                .toList();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }



}
