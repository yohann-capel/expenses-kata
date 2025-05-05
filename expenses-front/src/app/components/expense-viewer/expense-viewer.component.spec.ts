import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ExpenseViewerComponent } from './expense-viewer.component';
import { ExpenseService } from '../../services/expense.service';
import { ActivatedRoute } from '@angular/router';
import { Expense } from '../../models/expense';
import { ExpenseCategory } from '../../models/expense-category';
import { ExpenseFilter } from '../../models/expense-filter';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

@Component({
  selector: 'app-expense-creator',
  template: '<div class="mock-expense-creator"></div>',
})
class MockExpenseCreatorComponent {
  @Output() outputCreatedExpense = new EventEmitter<Expense>();
}

@Component({
  selector: 'app-filter-inputs',
  template: '<div class="mock-filter-inputs"></div>',
})
class MockFilterInputsComponent {
  @Output() filterEmiter = new EventEmitter<ExpenseFilter>();
}

@Component({
  selector: 'app-expense-card',
  template: '<div class="mock-expense-card"></div>',
})
class MockExpenseCardComponent {
  @Input() expense!: Expense;
  @Output() toDeleteId = new EventEmitter<number>();
}

@Component({
  selector: 'app-category-amount-viewer',
  template: '<div class="mock-category-amount-viewer"></div>',
})
class MockCategoryAmountViewerComponent {
  @Input() currentExpenseList!: Expense[];
}

describe('ExpenseViewerComponent', () => {
  let component: ExpenseViewerComponent;
  let fixture: ComponentFixture<ExpenseViewerComponent>;
  let expenseServiceSpy: jasmine.SpyObj<ExpenseService>;

  const mockExpenses: Expense[] = [
    {
      id: 1,
      amount: 100,
      category: ExpenseCategory.ALIMENTATION,
      date: '2025-05-06',
      description: 'Grocery shopping',
    },
    {
      id: 2,
      amount: 50,
      category: ExpenseCategory.TRANSPORTS,
      date: '2025-05-05',
      description: 'Bus ticket',
    },
    {
      id: 3,
      amount: 200,
      category: ExpenseCategory.LOISIRS,
      date: '2025-05-04',
      description: 'Cinema',
    },
  ];

  beforeEach(async () => {
    const expenseServiceSpyObj = jasmine.createSpyObj('ExpenseService', [
      'getAll',
      'getByFilter',
      'create',
      'delete',
    ]);

    await TestBed.configureTestingModule({
      declarations: [],
      imports: [
        ExpenseViewerComponent,
        MockExpenseCreatorComponent,
        MockFilterInputsComponent,
        MockExpenseCardComponent,
        MockCategoryAmountViewerComponent,
      ],
      providers: [
        { provide: HttpClient, useValue: provideHttpClientTesting },
        { provide: ExpenseService, useValue: expenseServiceSpyObj },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              data: { expenses: [...mockExpenses] },
            },
          },
        },
      ],
    }).compileComponents();

    expenseServiceSpy = TestBed.inject(
      ExpenseService,
    ) as jasmine.SpyObj<ExpenseService>;

    expenseServiceSpy.getByFilter.and.returnValue(of([...mockExpenses]));
    expenseServiceSpy.delete.and.returnValue(of());
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpenseViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with expenses from the route data', () => {
    expect(component.expenses.length).toBe(3);
    expect(component.expenses).toEqual(mockExpenses);
  });

  it('should filter expenses when loadFilteredData is called', fakeAsync(() => {
    const filteredExpenses: Expense[] = [mockExpenses[0]];
    expenseServiceSpy.getByFilter.and.returnValue(of(filteredExpenses));
    const filter: ExpenseFilter = { category: ExpenseCategory.ALIMENTATION };

    component.loadFilteredData(filter);
    tick();

    expect(expenseServiceSpy.getByFilter).toHaveBeenCalledWith(filter);
    expect(component.expenses).toEqual(filteredExpenses);
    expect(component.expenses.length).toBe(1);
  }));

  it('should add a new expense when getNewExpense is called', () => {
    const initialLength = component.expenses.length;
    const newExpense: Expense = {
      amount: 75,
      category: ExpenseCategory.LOISIRS,
      date: '2025-05-06',
      description: 'Rent',
    };

    component.getNewExpense(newExpense);

    expect(component.expenses.length).toBe(initialLength + 1);
    expect(component.expenses[initialLength]).toEqual(newExpense);
  });

  it('should not attempt to delete when id is undefined', fakeAsync(() => {
    const initialLength = component.expenses.length;

    component.deleteExpense(undefined);
    tick();

    expect(expenseServiceSpy.delete).not.toHaveBeenCalled();
    expect(component.expenses.length).toBe(initialLength);
  }));

  it('should render the expense creator component', () => {
    const creatorComponent = fixture.debugElement.query(
      By.css('app-expense-creator'),
    );
    expect(creatorComponent).toBeTruthy();
  });

  it('should render expense cards for each expense', () => {
    const expenseCards = fixture.debugElement.queryAll(
      By.css('app-expense-card'),
    );
    expect(expenseCards.length).toBe(component.expenses.length);
  });

  it('should render the category amount viewer component', () => {
    const categoryViewer = fixture.debugElement.query(
      By.css('app-category-amount-viewer'),
    );
    expect(categoryViewer).toBeTruthy();
  });
});
