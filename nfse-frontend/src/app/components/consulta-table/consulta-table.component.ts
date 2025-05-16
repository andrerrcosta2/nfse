import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-consulta-table',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './consulta-table.component.html',
  styleUrl: './consulta-table.component.scss',
  providers: [CurrencyPipe, DatePipe]
})
export class ConsultaTableComponent {
  @Input() resultados: any[] = [];

  displayedColumns: string[] = [
    'numeroCredito',
    'numeroNfse',
    'dataConstituicao',
    'valorIssqn',
    'tipoCredito',
    'simplesNacional',
    'aliquota',
    'valorFaturado',
    'valorDeducao',
    'baseCalculo'
  ];

  columnLabels: { [key: string]: string } = {
    numeroCredito: 'Número do Crédito',
    numeroNfse: 'Número da NFS-e',
    dataConstituicao: 'Data de Constituição',
    valorIssqn: 'Valor ISSQN',
    tipoCredito: 'Tipo de Crédito',
    simplesNacional: 'Simples Nacional',
    aliquota: 'Alíquota',
    valorFaturado: 'Valor Faturado',
    valorDeducao: 'Valor Dedução',
    baseCalculo: 'Base de Cálculo'
  };
}
