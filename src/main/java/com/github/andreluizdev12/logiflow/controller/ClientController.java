package com.github.andreluizdev12.logiflow.controller;


import com.github.andreluizdev12.logiflow.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.mappers.ClientMapper;
import com.github.andreluizdev12.logiflow.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper mapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientResponseDTO> create (@Valid @RequestBody CreateClientDTO createClientDTO) {
      var cliente =  clientService.create(createClientDTO);
        return ResponseEntity.ok(mapper.toResponse(cliente));

    }
}
