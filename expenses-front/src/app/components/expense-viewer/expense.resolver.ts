import { inject } from '@angular/core';
import { ResolveFn, Router } from '@angular/router';
import { ExpenseService } from '../../services/expense.service';
import { catchError, Observable } from 'rxjs';
import { Expense } from '../../models/expense';

export const expenseResolver: ResolveFn<Observable<Expense[]>> = (
  route,
  state,
) => {
  const service = inject(ExpenseService);
  const router = inject(Router);

  return service.getAll().pipe(
    catchError((err) => {
      alert(err.error);
      router.navigate(['']);
      throw err;
    }),
  );
};
