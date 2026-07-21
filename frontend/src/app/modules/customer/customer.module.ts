import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CustomerRoutingModule } from './customer-routing.module';
import { CustomerDashboardComponent } from './components/customer-dashboard/customer-dashboard.component';
import { BookCarComponent } from './components/book-car/book-car.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NzZorroImportsModule } from 'src/app/NzZorroImportsModule';
import { MyBookingsComponent } from './components/my-bookings/my-bookings.component';
import { CarSearchComponent } from './components/car-search/car-search.component';


@NgModule({
  declarations: [
    CustomerDashboardComponent,
    BookCarComponent,
    MyBookingsComponent,
    CarSearchComponent
  ],
  imports: [
    CommonModule,
    CustomerRoutingModule,
    NzZorroImportsModule,
    ReactiveFormsModule,FormsModule
  ]
})
export class CustomerModule { }
