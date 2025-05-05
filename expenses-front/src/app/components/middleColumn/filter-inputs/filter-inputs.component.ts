import { Component, output, viewChild } from '@angular/core';
import { ExpenseFilter } from '../../../models/expense-filter';
import { ExpenseCategory } from '../../../models/expense-category';
import { FormsModule } from '@angular/forms';
import { CategorySelectorComponent } from '../category-selector/category-selector.component';

@Component({
  selector: 'app-filter-inputs',
  imports: [CategorySelectorComponent, FormsModule],
  templateUrl: './filter-inputs.component.html',
  styleUrl: './filter-inputs.component.css',
})
export class FilterInputsComponent {
  filterEmiter = output<ExpenseFilter>();
  currentFilter: ExpenseFilter = {};
  categorySelector = viewChild.required(CategorySelectorComponent);

  filterAllExpenses(category: unknown) {
    if (category === '') {
      this.currentFilter.category = undefined;
      return;
    }
    this.currentFilter.category = category as ExpenseCategory;
  }

  loadFilteredExpenses() {
    this.filterEmiter.emit(this.currentFilter);
  }

  clearFilters() {
    this.currentFilter.category = undefined;
    this.currentFilter.startDate = undefined;
    this.currentFilter.endDate = undefined;
    this.categorySelector().reset();
    this.loadFilteredExpenses();
  }

  verifyDate(isStartDate: boolean, event: Event) {
    const calendarDate = (event.target as HTMLDataElement).value;
    if (
      isStartDate &&
      this.currentFilter.endDate !== undefined &&
      calendarDate > this.currentFilter.endDate
    ) {
      alert("Start date can't be after end date !");
      this.currentFilter.startDate = undefined;
    }

    if (
      !isStartDate &&
      this.currentFilter.startDate !== undefined &&
      calendarDate < this.currentFilter.startDate
    ) {
      alert("End date can't be before start date !");
      this.currentFilter.endDate = undefined;
    }
  }
}
