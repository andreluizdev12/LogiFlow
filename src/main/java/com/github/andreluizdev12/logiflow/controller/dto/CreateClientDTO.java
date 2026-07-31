package com.github.andreluizdev12.logiflow.controller.dto;

import com.github.andreluizdev12.logiflow.domain.client.PersonType;
import com.github.andreluizdev12.logiflow.domain.client.StatusClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateClientDTO(

        String externalId,

        Long sourceSystem,

        @NotNull(message = "O tipo de pessoa é obrigatório")
        PersonType personType,

        @NotBlank(message = "O nome é obrigatório")
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