import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Credito } from '../../interfaces/credito.interface';
import { MOCK_CREDITOS } from './creditos.mock';

@Injectable({
  providedIn: 'root'
})
export class ConsultaCreditoMockService {
  consultarPorNumeroNfse(numeroNfse: string): Observable<Credito[]> {
    const resultados = MOCK_CREDITOS.filter(c => c.numeroNfse === String(numeroNfse));
    return of(resultados);
  }

  consultarPorNumeroCredito(numeroCredito: string): Observable<Credito[]> {
    const resultado = MOCK_CREDITOS.find(c => c.numeroCredito === String(numeroCredito));
    return of(resultado ? [resultado] : []);
  }

  consultar(tipo: string, valor: string): Observable<Credito[]> {
    if (tipo === 'numeroNfse') {
      return this.consultarPorNumeroNfse(valor);
    } else if (tipo === 'numeroCredito') {
      return this.consultarPorNumeroCredito(valor);
    } else {
      return of([]);
    }
  }
}