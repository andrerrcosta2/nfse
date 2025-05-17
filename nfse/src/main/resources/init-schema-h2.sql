CREATE SCHEMA IF NOT EXISTS credito;

CREATE TABLE IF NOT EXISTS credito.credito (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  numero_credito VARCHAR(50) NOT NULL UNIQUE,
  numero_nfse VARCHAR(50) NOT NULL,
  data_constituicao DATE NOT NULL,
  valor_issqn DECIMAL(15, 2) NOT NULL,
  tipo_credito VARCHAR(50) NOT NULL,
  simples_nacional BOOLEAN NOT NULL,
  aliquota DECIMAL(5, 2) NOT NULL,
  valor_faturado DECIMAL(15, 2) NOT NULL,
  valor_deducao DECIMAL(15, 2) NOT NULL,
  base_calculo DECIMAL(15, 2) NOT NULL
);

INSERT INTO credito.credito (numero_credito, numero_nfse, data_constituicao, valor_issqn, tipo_credito, simples_nacional, aliquota, valor_faturado, valor_deducao, base_calculo)
SELECT * FROM (
  SELECT '123456', '7891011', DATE '2024-02-25', 1500.75, 'ISSQN', TRUE, 5.0, 30000.00, 5000.00, 25000.00
  UNION ALL SELECT '789012', '7891011', DATE '2024-02-26', 1200.50, 'ISSQN', FALSE, 4.5, 25000.00, 4000.00, 21000.00
  UNION ALL SELECT '654321', '1122334', DATE '2024-01-15', 800.50, 'Outros', TRUE, 3.5, 20000.00, 3000.00, 17000.00
) AS v(numero_credito, numero_nfse, data_constituicao, valor_issqn, tipo_credito, simples_nacional, aliquota, valor_faturado, valor_deducao, base_calculo)
WHERE NOT EXISTS (
  SELECT 1 FROM credito.credito c WHERE c.numero_credito = v.numero_credito
);