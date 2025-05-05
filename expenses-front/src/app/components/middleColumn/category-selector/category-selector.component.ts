import { Component, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExpenseCategory } from '../../../models/expense-category';

@Component({
  selector: 'app-category-selector',
  imports: [FormsModule],
  templateUrl: './category-selector.component.html',
  styleUrl: './category-selector.component.css',
})
export class CategorySelectorComponent {
  selectedValueEmiter = output<string>();
  categories: string[] = ['Choisir une catégorie', ...this.getAllCategories()];
  selectedOption: string = this.categories[0];

  getAllCategories(): string[] {
    return Object.keys(ExpenseCategory).filter((el) => isNaN(+el));
  }

  onCategoryChange() {
    console.log(this.selectedOption);
    this.selectedValueEmiter.emit(
      this.selectedOption === this.categories[0] ? '' : this.selectedOption,
    );
  }

  reset() {
    this.selectedOption = this.categories[0];
    this.onCategoryChange();
  }
}
