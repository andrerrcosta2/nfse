import { Credito } from "../../interfaces/credito.interface";

export const MOCK_CREDITOS: Credito[] = [
  {
    numeroCredito: '123456',
    numeroNfse: '7891011',
    dataConstituicao: '2024-02-25',
    valorIssqn: 1500.75,
    tipoCredito: 'ISSQN',
    simplesNacional: 'Sim',
    aliquota: 5.0,
    valorFaturado: 30000.0,
    valorDeducao: 5000.0,
    baseCalculo: 25000.0
  },
  {
    numeroCredito: '789012',
    numeroNfse: '7891011',
    dataConstituicao: '2024-02-26',
    valorIssqn: 1200.5,
    tipoCredito: 'ISSQN',
    simplesNacional: 'Não',
    aliquota: 4.5,
    valorFaturado: 25000.0,
    valorDeducao: 4000.0,
    baseCalculo: 21000.0
  },
  {
    numeroCredito: '654321',
    numeroNfse: '1122334',
    dataConstituicao: '2024-01-15',
    valorIssqn: 800.5,
    tipoCredito: 'Outros',
    simplesNacional: 'Sim',
    aliquota: 3.5,
    valorFaturado: 20000.0,
    valorDeducao: 3000.0,
    baseCalculo: 17000.0
  }
];