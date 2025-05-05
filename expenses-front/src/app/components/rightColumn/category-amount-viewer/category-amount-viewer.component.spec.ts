import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryAmountViewerComponent } from './category-amount-viewer.component';
import { CurrencyPipe } from '@angular/common';

describe('CategoryAmountViewerComponent', () => {
  let component: CategoryAmountViewerComponent;
  let fixture: ComponentFixture<CategoryAmountViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryAmountViewerComponent, CurrencyPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryAmountViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
