import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterLeisteComponent } from './filter-leiste.component';

describe('FilterLeisteComponent', () => {
  let component: FilterLeisteComponent;
  let fixture: ComponentFixture<FilterLeisteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilterLeisteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FilterLeisteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
