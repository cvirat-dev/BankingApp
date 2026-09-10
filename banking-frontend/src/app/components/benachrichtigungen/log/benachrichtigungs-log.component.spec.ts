import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenachrichtigungsLogComponent } from './benachrichtigungs-log.component';

describe('BenachrichtigungsLogComponent', () => {
  let component: BenachrichtigungsLogComponent;
  let fixture: ComponentFixture<BenachrichtigungsLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BenachrichtigungsLogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BenachrichtigungsLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
