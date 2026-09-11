import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KontoEditComponent } from './konto-edit.component';

describe('KontoEditComponent', () => {
  let component: KontoEditComponent;
  let fixture: ComponentFixture<KontoEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [KontoEditComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(KontoEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
