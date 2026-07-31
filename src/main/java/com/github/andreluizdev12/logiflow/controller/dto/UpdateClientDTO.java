package com.github.andreluizdev12.logiflow.controller.dtos;

import com.github.andreluizdev12.logiflow.domain.client.PersonType;
import com.github.andreluizdev12.logiflow.domain.client.StatusClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateClientDTO(

        @Size(
                min = 3,
                max = 150,
                message = "O nome deve ter entre 3 e 150 caracteres"
        )
        String name,

        String documento,

        @Pattern(
                regexp = "^[0-9]+$",
                message = "O telefone deve conter apenas números"
        )
        String telefone,

        @Email(message = "Formato de e-mail inválido")
        String email,

        StatusClient status
) {
}