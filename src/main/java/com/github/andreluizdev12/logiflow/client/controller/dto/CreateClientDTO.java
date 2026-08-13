package com.github.andreluizdev12.logiflow.client.controller.dto;

import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateClientDTO(
        @NotBlank(message = "O identificador externo é obrigatório")
        String externalId,

        @NotBlank(message = "O sistema de origem é obrigatório")
        String  sourceSystem,

        @NotNull(message = "O tipo de pessoa é obrigatório")
        PersonType personType,

        @NotBlank(message = "O nome é obrigatório")
        @Size(
                min = 3,
                max = 150,
                message = "O nome deve ter entre 3 e 150 caracteres"
        )
        String name,

        @NotBlank(message = "O documento é obrigatório")
        String document,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Email(message = "Formato de e-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email

) {
}
