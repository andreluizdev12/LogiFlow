package com.github.andreluizdev12.logiflow.controller;


import com.github.andreluizdev12.logiflow.controller.dto.ClientResponseDTO;
import com.github.andreluizdev12.logiflow.controller.dto.CreateClientDTO;
import com.github.andreluizdev12.logiflow.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    private ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientResponseDTO> create (@Valid @RequestBody CreateClientDTO createClientDTO) {
      var cliente =  clientService.create(createClientDTO);
      return ResponseEntity.body(cliente)
    }
}
