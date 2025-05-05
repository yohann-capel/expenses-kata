import { ExpenseCategory } from './expense-category';

export interface ExpenseFilter {
  category?: ExpenseCategory;
  startDate?: string;
  endDate?: string;
}
