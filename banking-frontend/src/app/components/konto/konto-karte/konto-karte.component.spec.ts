import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KontoKarteComponent } from './konto-karte.component';

describe('KontoKarteComponent', () => {
  let component: KontoKarteComponent;
  let fixture: ComponentFixture<KontoKarteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [KontoKarteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(KontoKarteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
