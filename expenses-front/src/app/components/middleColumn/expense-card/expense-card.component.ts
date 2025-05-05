import { Component, input, InputSignal, output, Signal } from '@angular/core';
import { Expense } from '../../../models/expense';
import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { ExpenseCategory } from '../../../models/expense-category';

@Component({
  selector: 'app-expense-card',
  imports: [CurrencyPipe, TitleCasePipe, DatePipe],
  templateUrl: './expense-card.component.html',
  styleUrl: './expense-card.component.css',
})
export class ExpenseCardComponent {
  toDeleteId = output<number | undefined>();
  expense: InputSignal<Expense> = input<Expense>({
    id: 0,
    description: '',
    amount: 0,
    date: '0000-00-00',
    category: ExpenseCategory.ALIMENTATION,
  } as Expense);

  deleteExpense() {
    this.toDeleteId.emit(this.expense().id);
  }
}
