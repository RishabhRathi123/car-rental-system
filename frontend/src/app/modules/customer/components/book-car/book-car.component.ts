import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StorageService } from 'src/app/auth/services/storage/storage.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-book-car',
  templateUrl: './book-car.component.html',
  styleUrls: ['./book-car.component.scss']
})
export class BookCarComponent {

  carId: number = this.activatedRoute.snapshot.params['id'];
  car: any;
  processedImage: any;
  validateForm!: FormGroup;
  isSpinning: boolean = false;
  dateFormat: "DD-MM-YYYY" | "YYYY-MM-DD" = "DD-MM-YYYY";

  constructor(
    private service: CustomerService,
    private activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    private router: Router,
    private message: NzMessageService,
  ) {}

  ngOnInit() {
    this.getCarById();
    const queryParams = this.activatedRoute.snapshot.queryParams;
    const startDate = queryParams['startDate'];
    const endDate = queryParams['endDate'];

    this.validateForm = this.fb.group({
      startDate: [startDate ? new Date(startDate) : null, Validators.required],
      endDate: [endDate ? new Date(endDate) : null, Validators.required]
    });
  }

  getCarById() {
    this.service.getCarById(this.carId).subscribe({
      next: (response) => {
        this.car = response;
        this.processedImage = 'data:image/jpeg;base64,' + response.returnedImage;
      },
      error: (error) => {
        console.error('Error fetching car details:', error);
      }
    });
  }

  bookACar(data: any) {
    this.isSpinning = true;
    const bookingDto = {
      carId: this.carId,
      userId: StorageService.getUserId(),
      startDate: data.startDate,
      endDate: data.endDate
    };

    this.service.bookACar(bookingDto).subscribe({
      next: () => {
        this.message.success('Booking request submitted for approval!');
        this.router.navigateByUrl('/customer/dashboard');
        this.isSpinning = false;
      },
      error: () => {
        this.message.error('Booking request failed.');
        this.isSpinning = false;
      }
    });
  }
}
