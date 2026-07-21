import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-customer-dashboard',
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.scss']
})
export class CustomerDashboardComponent implements OnInit {
  bookings: any[] = [];

  constructor(private service: CustomerService,
    private message: NzMessageService
  ) {}

  ngOnInit() {
    this.getBookings();
  }

  deleteBooking(bookingId: number) {
  if (confirm('Are you sure you want to delete this booking?')) {
    this.service.deleteBooking(bookingId).subscribe({
      next: () => {
        this.bookings = this.bookings.filter(b => b.id !== bookingId);
      },
      error: (err) => {
        console.error('Failed to delete booking', err);
        this.message.success('Refresh to see updated bookings');
      }
    });
  }
}

  getBookings() {
    this.service.getBookingsByUserId().subscribe({
      next: (res) => {
        console.log("Bookings: ", res);
        this.bookings = res.map((b: any) => ({
          ...b,
          processedImg: b.returnedImage ? 'data:image/jpeg;base64,' + b.returnedImage : ''

        }));
      },
      error: (err) => {
        console.error('Booking fetch failed', err);
      }
    });
  }
}
