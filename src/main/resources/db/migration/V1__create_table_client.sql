CREATE TABLE client (
                        id UUID NOT NULL,
                        external_id VARCHAR(255) NOT NULL,
                        source_system VARCHAR(255) NOT NULL,
                        tipo_pessoa VARCHAR(50) NOT NULL,
                        nome VARCHAR(255) NOT NULL,
                        documento VARCHAR(30) NOT NULL,
                        telefone VARCHAR(20),
                        email VARCHAR(255) NOT NULL,
                        status VARCHAR(50),
                        criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT chk_client_telefone_apenas_numeros  CHECK (telefone IS NULL OR telefone ~ '^[0-9]+$'),
                        CONSTRAINT chk_client_nome_tamanho CHECK (length(trim(nome)) BETWEEN 3 AND 150),
                        CONSTRAINT pk_client PRIMARY KEY (id),
                        CONSTRAINT chk_client_email_lowercase CHECK (email = LOWER(email)),
                        CONSTRAINT uq_client_external_id_source_system UNIQUE (external_id, source_system)
);

-- Índices recomendados para otimização de busca
CREATE INDEX idx_client_external_id ON client(external_id);
CREATE INDEX idx_client_documento ON client(documento);
CREATE INDEX idx_client_email ON client(email);