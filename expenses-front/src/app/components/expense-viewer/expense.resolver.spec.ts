import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { expenseResolver } from './expense.resolver';
import { Expense } from '../../models/expense';
import { Observable } from 'rxjs';

describe('expenseResolver', () => {
  const executeResolver: ResolveFn<Observable<Expense[]>> = (
    ...resolverParameters
  ) =>
    TestBed.runInInjectionContext(() => expenseResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
