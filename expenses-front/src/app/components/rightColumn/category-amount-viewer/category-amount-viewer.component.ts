import {
  Component,
  computed,
  inject,
  input,
  InputSignal,
  signal,
  WritableSignal,
} from '@angular/core';
import { ExpenseCategory } from '../../../models/expense-category';
import { ExpenseService } from '../../../services/expense.service';
import { CurrencyPipe } from '@angular/common';
import { Expense } from '../../../models/expense';

@Component({
  selector: 'app-category-amount-viewer',
  imports: [CurrencyPipe],
  templateUrl: './category-amount-viewer.component.html',
  styleUrl: './category-amount-viewer.component.css',
})
export class CategoryAmountViewerComponent {
  currentExpenseList = input([] as Expense[]);
  categoryAmountMap = signal(new Map<number, Expense>());
  total = computed(() => {
    return Array.from(this.categoryAmountMap().values()).reduce(
      (sum, expense) => sum + expense.amount,
      0,
    );
  });

  ngOnInit() {
    console.log(this.currentExpenseList());
    this.loadAllAmounts();
  }

  getAllCategories(): string[] {
    return Object.keys(ExpenseCategory).filter((el) => isNaN(+el));
  }

  amountForCategory(category: string): number {
    return Array.from(this.categoryAmountMap().values())
      .filter((expense) => expense.category === (category as ExpenseCategory))
      .reduce((sum, expense) => (sum += expense.amount), 0);
  }

  loadAllAmounts(): void {
    this.currentExpenseList().forEach((el) => {
      const updatedMap = new Map(this.categoryAmountMap());
      updatedMap.set(el.id!, el);
      this.categoryAmountMap.set(updatedMap);
    });
  }
}
