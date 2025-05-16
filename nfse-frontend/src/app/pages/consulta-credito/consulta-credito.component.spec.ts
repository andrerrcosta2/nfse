import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultaCreditoComponent } from './consulta-credito.component';

describe('ConsultaCreditoComponent', () => {
  let component: ConsultaCreditoComponent;
  let fixture: ComponentFixture<ConsultaCreditoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultaCreditoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultaCreditoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
