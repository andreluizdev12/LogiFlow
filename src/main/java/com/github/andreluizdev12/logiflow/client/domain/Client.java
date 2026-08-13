package com.github.andreluizdev12.logiflow.client.domain;

import com.github.andreluizdev12.logiflow.client.domain.converter.DocumentConverter;
import com.github.andreluizdev12.logiflow.client.domain.enums.PersonType;
import com.github.andreluizdev12.logiflow.client.domain.enums.StatusClient;
import com.github.andreluizdev12.logiflow.client.domain.vos.Document;
import com.github.andreluizdev12.logiflow.client.domain.vos.Email;
import com.github.andreluizdev12.logiflow.client.exceptions.InvalidDocumentForPersonTypeException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "client")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "source_system", nullable = false)
    private String  sourceSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 20)
    private PersonType personType;

    @Column(name = "nome", nullable = false, length = 150)
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String name;

    @Column(name = "documento", nullable = false, length = 14)
    @Convert(converter = DocumentConverter.class)
    private Document document;

    @Pattern(regexp = "^[0-9]+$", message = "O telefone deve conter apenas números")
    @Column(name = "telefone", length = 11)
    @Setter(AccessLevel.NONE)
    private String telefone;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "email", nullable = false, length = 150)
    )
    private Email email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Setter(AccessLevel.NONE)
    private StatusClient status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "atualizado_em", nullable = false)
    private Instant updatedOn;

    private Client(String externalId, String sourceSystem, PersonType personType, String name, String document, String telefone, String email) {
        this.id = UUID.randomUUID();
        this.externalId = normalizeRequired(externalId, "O identificador externo é obrigatório");
        this.sourceSystem = normalizeRequired(sourceSystem, "O sistema de origem é obrigatório");
        this.personType = requirePersonType(personType);
        changeName(name);
        this.telefone =  normalizePhone(telefone);
        this.email =  Email.of(email);
        this.document = Document.of(document);
        validateDocumentType();
        this.status = StatusClient.ATIVO;
        this.createdOn = Instant.now();
        this.updatedOn =  Instant.now();
    }
    public static Client build(String externalId, String sourceSystem, PersonType personType, String name, String documento, String telefone, String email) {
        return  new Client(externalId,sourceSystem,personType,name,documento,telefone,email);
    }


    private void validateDocumentType() {
        if (personType == PersonType.PESSOA_FISICA && !document.isCpf()) {
            throw new InvalidDocumentForPersonTypeException(
                    "Pessoa física deve possuir CPF"
            );
        }

        if (personType == PersonType.PESSOA_JURIDICA && !document.isCnpj()) {
            throw new InvalidDocumentForPersonTypeException(
                    "Pessoa jurídica deve possuir CNPJ"
            );
        }
    }


    public void changePhone(String phone) {
        this.telefone = normalizePhone(phone);
        this.updatedOn = Instant.now();
    }


    public void update(String name,  String email, String telefone) {
        if(name != null) {
            changeName(name);
        }
        if(email != null) {
            changeEmail(email);
        }
        if(telefone != null) {
            changePhone(telefone);
        }
        this.updatedOn = Instant.now();
    }


    public void changeEmail(String email) {
        this.email = Email.of(email);
        this.updatedOn = Instant.now();
    }

    public void ativar() {
        this.status =StatusClient.ATIVO;
        this.updatedOn = Instant.now();
    }


    public void desativar() {
        this.status =StatusClient.INATIVO;
        this.updatedOn = Instant.now();
    }

    private static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String normalized = phone
                .trim()
                .replaceAll("[()\\-\\s]", "");

        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "O telefone deve conter somente números"
            );
        }


        if (normalized.length() < 10 || normalized.length() > 11) {
            throw new IllegalArgumentException(
                    "O telefone deve possuir 10 ou 11 dígitos"
            );
        }

        return normalized;
    }

    private static String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static PersonType requirePersonType(PersonType personType) {
        if (personType == null) {
            throw new IllegalArgumentException("O tipo de pessoa é obrigatório");
        }
        return personType;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", personType=" + personType +
                ", name='" + name + '\'' +
                ", document=" + document +
                ", telefone='" + telefone + '\'' +
                ", email=" + email +
                ", status=" + status +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }

    public void changeName(String name){

        if (name == null || name.trim().length() < 3 || name.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "O nome deve ter entre 3 e 150 caracteres"
            );
        }
        this.name = name.trim();
    }


}
