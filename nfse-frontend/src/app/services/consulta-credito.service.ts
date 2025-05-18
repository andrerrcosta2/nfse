import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { Credito } from '../interfaces/credito.interface';
import { environment } from '../../environments/environment-prod';

@Injectable({
  providedIn: 'root'
})
export class ConsultaCreditoService {
  private apiUrl = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {}

  consultarPorNumeroNfse(numeroNfse: string): Observable<Credito[]> {
      return this.http.get<Credito[]>(`${this.apiUrl}/${numeroNfse}`);
    }
  
    consultarPorNumeroCredito(numeroCredito: string): Observable<Credito>{
      return this.http.get<Credito>(`${this.apiUrl}/credito/${numeroCredito}`);
    }

  consultar(tipo: string, valor: string): Observable<Credito[]> {
    switch (tipo) {
      case 'numeroNfse':
        return this.consultarPorNumeroNfse(valor);
      case 'numeroCredito':
        return this.consultarPorNumeroCredito(valor).pipe(
          map(credito => [credito])
        );
      default:
        return of([]);
    }
    
  }
}
