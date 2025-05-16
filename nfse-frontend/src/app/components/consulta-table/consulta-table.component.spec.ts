import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultaTableComponent } from './consulta-table.component';

describe('ConsultaTableComponent', () => {
  let component: ConsultaTableComponent;
  let fixture: ComponentFixture<ConsultaTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultaTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultaTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
