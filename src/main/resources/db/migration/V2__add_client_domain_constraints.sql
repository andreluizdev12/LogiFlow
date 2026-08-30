UPDATE client
SET status = 'ATIVO'
WHERE status IS NULL;

UPDATE client
SET documento = regexp_replace(documento, '[^0-9]', '', 'g'),
    nome = trim(nome);

DROP INDEX IF EXISTS idx_client_documento;

ALTER TABLE client
    ALTER COLUMN tipo_pessoa TYPE VARCHAR(20),
    ALTER COLUMN nome TYPE VARCHAR(150),
    ALTER COLUMN documento TYPE VARCHAR(14),
    ALTER COLUMN telefone TYPE VARCHAR(11),
    ALTER COLUMN email TYPE VARCHAR(150),
    ALTER COLUMN status TYPE VARCHAR(20),
    ALTER COLUMN status SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_client_documento'
          AND conrelid = 'client'::regclass
    ) THEN
        ALTER TABLE client
            ADD CONSTRAINT uq_client_documento UNIQUE (documento);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_client_tipo_pessoa'
          AND conrelid = 'client'::regclass
    ) THEN
        ALTER TABLE client
            ADD CONSTRAINT chk_client_tipo_pessoa
                CHECK (tipo_pessoa IN ('PESSOA_FISICA', 'PESSOA_JURIDICA'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_client_status'
          AND conrelid = 'client'::regclass
    ) THEN
        ALTER TABLE client
            ADD CONSTRAINT chk_client_status
                CHECK (status IN ('ATIVO', 'INATIVO'));
    END IF;
END $$;
