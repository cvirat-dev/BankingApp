import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransaktionFormularComponent } from './transaktion-formular.component';

describe('TransaktionFormularComponent', () => {
  let component: TransaktionFormularComponent;
  let fixture: ComponentFixture<TransaktionFormularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransaktionFormularComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransaktionFormularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
