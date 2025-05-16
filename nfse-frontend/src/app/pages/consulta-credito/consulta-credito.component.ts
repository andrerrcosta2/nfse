import { Component } from '@angular/core';
import { ConsultaCreditoService } from '../../services/consulta-credito.service';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ConsultaTableComponent } from '../../components/consulta-table/consulta-table.component';

@Component({
  selector: 'app-consulta-credito',
  templateUrl: './consulta-credito.component.html',
  styleUrl: './consulta-credito.component.scss',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTableModule,
    ConsultaTableComponent
  ]
})
export class ConsultaCreditoComponent {

  searchType: string = 'numeroNfse';
  searchValue: string = '';
  resultados: any[] = [];

  constructor(private consultaService: ConsultaCreditoService) { }

  consultar() {
    this.consultaService.consultar(this.searchType, this.searchValue).subscribe(data => {
      this.resultados = data;
    });
  }
}
