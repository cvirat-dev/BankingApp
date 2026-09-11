import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KontoErstellenComponent } from './konto-erstellen.component';

describe('KontoErstellenComponent', () => {
  let component: KontoErstellenComponent;
  let fixture: ComponentFixture<KontoErstellenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [KontoErstellenComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(KontoErstellenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
