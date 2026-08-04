package com.github.andreluizdev12.logiflow.repositorys;

import com.github.andreluizdev12.logiflow.domain.client.Client;
import com.github.andreluizdev12.logiflow.domain.client.vos.Document;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByDocument(Document document);
}
