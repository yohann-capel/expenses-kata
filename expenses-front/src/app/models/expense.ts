import { ExpenseCategory } from './expense-category';

export interface Expense {
  id?: number;
  amount: number;
  category: ExpenseCategory;
  date: string;
  description?: string;
}
