import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuchungFormularComponent } from './buchung-formular.component';

describe('BuchungFormularComponent', () => {
  let component: BuchungFormularComponent;
  let fixture: ComponentFixture<BuchungFormularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BuchungFormularComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BuchungFormularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
