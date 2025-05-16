import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { ConsultaCreditoService } from './services/consulta-credito.service';
import { environment } from '../environments/environment';
import { ConsultaCreditoMockService } from './services/mocks/consulta-credito-mock.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideHttpClient(),
    {
      provide: ConsultaCreditoService,
      useClass: environment.useMocks ? ConsultaCreditoMockService : ConsultaCreditoService
    }
  ]
};
