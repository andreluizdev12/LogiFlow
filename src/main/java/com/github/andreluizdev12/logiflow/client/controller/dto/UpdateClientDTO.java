package com.github.andreluizdev12.logiflow.client.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateClientDTO(

        @Size(min = 3, max = 150)
        String name,

        @Pattern(
                regexp = "^[0-9]+$",
                message = "O telefone deve conter apenas números"
        )
        String telefone,

        @Email(message = "Formato de e-mail inválido")
        String email
) {
}