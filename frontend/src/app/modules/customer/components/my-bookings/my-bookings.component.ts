import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { NzMessageService } from 'ng-zorro-antd/message';
declare var Razorpay: any;

@Component({
  selector: 'app-my-bookings',
  templateUrl: './my-bookings.component.html',
  styleUrls: ['./my-bookings.component.scss']
})
export class MyBookingsComponent {

  bookings: any;
  isSpinning: boolean = false;

  constructor(
    private service: CustomerService,
    private message: NzMessageService
  ) {
    this.getMyBookings();
  }

  getMyBookings() {
    this.isSpinning = true;
    this.service.getBookingsByUserId().subscribe({
      next: (response) => {
        this.bookings = response;
        this.isSpinning = false;
      },
      error: (error) => {
        this.message.error('Failed to load bookings');
        this.isSpinning = false;
      }
    });
  }

  payNow(booking: any) {
    const receipt = `car-booking-${booking.id}-${Date.now()}`;
    // Send the amount in rupees; the backend converts rupees -> paise when it
    // creates the Razorpay order. (Previously the frontend also multiplied by
    // 100, which double-converted and overcharged 100x.)
    const amountInRupees = booking.price;

    this.service.createOrder(amountInRupees, receipt).subscribe({
      next: (order: any) => {
        const options = {
          key: 'rzp_test_OVYMnXur6tEFGV',
          // Use the amount the backend/Razorpay actually created (in paise).
          amount: order.amount,
          currency: order.currency || 'INR',
          name: 'Car Rental',
          description: 'Car Booking Payment',
          order_id: order.id,
          handler: (response: any) => {
            this.verifyPayment(response, booking.id);
          },
          prefill: {
            name: booking.username,
            email: booking.email,
          },
          theme: { color: '#1677ff' }
        };

        const razorpay = new Razorpay(options);
        razorpay.open();
      },
      error: () => {
        this.message.error('Failed to create payment order');
      }
    });
  }

  verifyPayment(paymentResponse: any, bookingId: number) {
  const data = {
    razorpay_order_id: paymentResponse.razorpay_order_id,
    razorpay_payment_id: paymentResponse.razorpay_payment_id,
    razorpay_signature: paymentResponse.razorpay_signature,
    bookingId: bookingId
  };
console.log("Sending payment verification request with data:", data);
  this.service.verifyPayment(data).subscribe({
    next: () => {
      this.message.success('Payment successful & booking confirmed!');
      this.getMyBookings();
    },
    error: () => {
      this.message.error('Payment verification failed');
    }
  });
}

}


