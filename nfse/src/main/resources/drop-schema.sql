DELETE FROM credito.credito
WHERE numero_credito IN ('123456', '789012', '654321');

DO $$
BEGIN
    IF EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'credito'
          AND table_name = 'credito'
    ) THEN
        EXECUTE 'DROP TABLE credito.credito';
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT schema_name
        FROM information_schema.schemata
        WHERE schema_name = 'credito'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'credito'
    ) THEN
        EXECUTE 'DROP SCHEMA credito';
    END IF;
END
$$;
