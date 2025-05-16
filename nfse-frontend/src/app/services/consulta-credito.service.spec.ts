import { TestBed } from '@angular/core/testing';

import { ConsultaCreditoService } from './consulta-credito.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Credito } from '../interfaces/credito.interface';

describe('ConsultaCreditoService', () => {
  let service: ConsultaCreditoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ConsultaCreditoService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(ConsultaCreditoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // ensure no unmatched requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch créditos by númeroNfse', () => {
    const mockData = [
      {
        numeroCredito: '123456',
        numeroNfse: '7891011',
        dataConstituicao: '2024-02-25',
        valorIssqn: 1500.75,
        tipoCredito: 'ISSQN',
        simplesNacional: 'Sim',
        aliquota: 5.0,
        valorFaturado: 30000.00,
        valorDeducao: 5000.00,
        baseCalculo: 25000.00
      }
    ];

    service.consultarPorNumeroNfse('7891011').subscribe(result => {
      expect(result).toEqual(mockData);
    });

    const req = httpMock.expectOne('/api/creditos/7891011');
    expect(req.request.method).toBe('GET');
    req.flush(mockData); // Respond with mock data
  });

  it('should fetch crédito by númeroCredito', () => {
    const mockCredito: Credito = {
      numeroCredito: '123456',
      numeroNfse: '7891011',
      dataConstituicao: '2024-02-25',
      valorIssqn: 1500.75,
      tipoCredito: 'ISSQN',
      simplesNacional: 'Sim',
      aliquota: 5.0,
      valorFaturado: 30000.00,
      valorDeducao: 5000.00,
      baseCalculo: 25000.00
    };

    service.consultarPorNumeroCredito('123456').subscribe(result => {
      expect(result).toEqual(mockCredito);
    });

    const req = httpMock.expectOne('/api/creditos/credito/123456');
    expect(req.request.method).toBe('GET');
    req.flush(mockCredito);
  });
});
