import { Routes } from '@angular/router';
import { ConsultaCreditoComponent } from './pages/consulta-credito/consulta-credito.component';

export const routes: Routes = [
    {
    path: '',
    loadComponent: () => import('./pages/consulta-credito/consulta-credito.component')
      .then(m => m.ConsultaCreditoComponent)
  }
];
