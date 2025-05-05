import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseCreatorComponent } from './expense-creator.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';

describe('ExpenseCreatorComponent', () => {
  let component: ExpenseCreatorComponent;
  let fixture: ComponentFixture<ExpenseCreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseCreatorComponent],
      providers: [
        { provide: HttpClient, useValue: provideHttpClientTesting() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ExpenseCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
