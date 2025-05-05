import { inject, Injectable, WritableSignal } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense } from '../models/expense';
import { ExpenseFilter } from '../models/expense-filter';
import { ExpenseCategory } from '../models/expense-category';

@Injectable({
  providedIn: 'root',
})
export class ExpenseService {
  private api: string = environment.apiUrl + '/expenses';
  private httpClient = inject(HttpClient);

  constructor() {}

  getAll(): Observable<Expense[]> {
    return this.httpClient.get<Expense[]>(this.api);
  }

  getById(id: number): Observable<Expense> {
    return this.httpClient.get<Expense>(`${this.api}/${id}`);
  }

  getByFilter(filter: ExpenseFilter) {
    let params = new HttpParams();

    if (filter.category) {
      params = params.set('category', filter.category);
    }
    if (filter.startDate) {
      params = params.set('startDate', filter.startDate);
    }
    if (filter.endDate) {
      params = params.set('endDate', filter.endDate);
    }
    return this.httpClient.get<Expense[]>(`${this.api}/filtered`, { params });
  }

  amountByCategory(category: ExpenseCategory): Observable<number> {
    return this.httpClient.get<number>(`${this.api}/amount/${category}`);
  }

  create(expense: Expense): Observable<Expense> {
    return this.httpClient.post<Expense>(this.api, expense);
  }

  update(expense: Expense): Observable<Expense> {
    return this.httpClient.put<Expense>(this.api, expense);
  }

  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.api}/${id}`);
  }
}
