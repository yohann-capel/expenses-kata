import { Component, inject } from '@angular/core';
import { ExpenseService } from '../../services/expense.service';
import { Expense } from '../../models/expense';
import { ExpenseCategory } from '../../models/expense-category';
import { switchMap } from 'rxjs';
import { ExpenseCardComponent } from '../middleColumn/expense-card/expense-card.component';
import { ExpenseFilter } from '../../models/expense-filter';
import { FilterInputsComponent } from '../middleColumn/filter-inputs/filter-inputs.component';
import { CategoryAmountViewerComponent } from '../rightColumn/category-amount-viewer/category-amount-viewer.component';
import { ActivatedRoute } from '@angular/router';
import { ExpenseCreatorComponent } from '../leftColumn/expense-creator/expense-creator.component';

@Component({
  selector: 'app-expense-viewer',
  imports: [
    ExpenseCardComponent,
    FilterInputsComponent,
    CategoryAmountViewerComponent,
    ExpenseCreatorComponent,
  ],
  templateUrl: './expense-viewer.component.html',
  styleUrl: './expense-viewer.component.css',
})
export class ExpenseViewerComponent {
  service: ExpenseService = inject(ExpenseService);
  activatedRoute = inject(ActivatedRoute);
  expenses: Expense[] = this.activatedRoute.snapshot.data['expenses'];
  currentFilter: ExpenseFilter = {};

  getCurrentDate() {
    return new Date().toISOString().split('T')[0];
  }

  loadFilteredData(filter: ExpenseFilter) {
    this.service
      .getByFilter(filter)
      .subscribe({ next: (data) => (this.expenses = data) });
  }

  getNewExpense(expense: Expense) {
    this.expenses.push(expense);
  }

  deleteExpense(id: number | undefined) {
    if (id === undefined) return;

    this.service.delete(id).subscribe((_) => {
      this.expenses = this.expenses.filter((el) => el.id !== id);
    });
  }
}
