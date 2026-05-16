import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenachrichtigungItemComponent } from './benachrichtigung-item.component';

describe('BenachrichtigungItemComponent', () => {
  let component: BenachrichtigungItemComponent;
  let fixture: ComponentFixture<BenachrichtigungItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BenachrichtigungItemComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BenachrichtigungItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
