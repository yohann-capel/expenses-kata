import { Component, inject, output } from '@angular/core';
import { Expense } from '../../../models/expense';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ExpenseCategory } from '../../../models/expense-category';
import { ExpenseService } from '../../../services/expense.service';

@Component({
  selector: 'app-expense-creator',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './expense-creator.component.html',
  styleUrl: './expense-creator.component.css',
})
export class ExpenseCreatorComponent {
  private service = inject(ExpenseService);
  private fb = inject(FormBuilder);
  outputCreatedExpense = output<Expense>();
  categories: string[] = [...this.getAllCategories()];
  expenseCreationForm = this.fb.group({
    amount: [0, [Validators.required, this.isNumberOnly(), Validators.min(1)]],
    description: ['', [Validators.maxLength(150)]],
    category: [
      ExpenseCategory.ALIMENTATION,
      [Validators.required, this.isValidCategory()],
    ],
    date: ['', [Validators.required, this.verifyDate()]],
  });

  isNumberOnly(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      return isNaN(control.value) ? { amount: { value: control.value } } : null;
    };
  }

  verifyDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const date = new Date(control.value);
      return date > new Date(Date.now())
        ? { date: { value: control.value } }
        : null;
    };
  }

  isValidCategory(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;

      const category: ExpenseCategory = control.value as ExpenseCategory;
      const length = this.getAllCategories().filter(
        (el) => el === category,
      ).length;

      return length !== 1 ? { category: { value: control.value } } : null;
    };
  }

  showModal(modal: HTMLElement) {
    modal.classList.remove('hidden');
  }

  hideModal(modal: HTMLElement) {
    modal.classList.add('hidden');
  }

  getAllCategories(): string[] {
    return Object.keys(ExpenseCategory).filter((el) => isNaN(+el));
  }

  onSubmit(modal: HTMLElement) {
    if (this.expenseCreationForm.valid) {
      const result: Expense = { ...this.expenseCreationForm.value } as Expense;

      this.service.create(result).subscribe({
        next: (data: Expense) => {
          this.expenseCreationForm.reset();
          this.hideModal(modal);
          this.outputCreatedExpense.emit(data);
        },
        error: (err) => alert(err),
      });
    } else {
      alert('invalid form');
    }
  }
}
