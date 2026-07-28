package com.github.andreluizdev12.logiflow.domain.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "source_system")
    private Long sourceSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    @NotNull(message = "O tipo de pessoa é obrigatório")
    private PersonType personType;

    @Column(name = "nome", nullable = false, length = 150)
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String name;

    private String documento;

    @Pattern(regexp = "^[0-9]+$", message = "O telefone deve conter apenas números")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusClient status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "atualizado_em", nullable = false)
    private Instant updatedOn;
}
