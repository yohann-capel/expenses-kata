import { Routes } from '@angular/router';
import { ExpenseViewerComponent } from './components/expense-viewer/expense-viewer.component';
import { expenseResolver } from './components/expense-viewer/expense.resolver';

export const routes: Routes = [
  {
    path: '',
    component: ExpenseViewerComponent,
    resolve: {
      expenses: expenseResolver,
    },
  },
];
