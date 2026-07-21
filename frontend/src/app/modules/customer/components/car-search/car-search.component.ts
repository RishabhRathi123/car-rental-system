import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CarService, CarResponse } from '../../service/car.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-car-search',
  templateUrl: './car-search.component.html',
  styleUrls: ['./car-search.component.scss']
})
export class CarSearchComponent implements OnInit {
  submitted = false;
  filterForm!: FormGroup;
  cars: CarResponse[] = [];
  latitude!: number;
  longitude!: number;
  loading = false;

  listOfBrands = [
    "BMW", "AUDI", "FERRARI", "TESLA", "VOLVO", "TOYOTA", 
    "HONDA", "FORD", "NISSAN", "HYUNDAI", "LEXUS", "KIA"
  ];

  listOfType = ["Petrol", "Hybrid", "Diesel", "Electric", "CNG"];
  listOfColor = ["Red", "White", "Blue", "Black", "Orange", "Grey", "Silver"];
  listOfTransmission = ["Manual", "Automatic"];

  constructor(private fb: FormBuilder, private carService: CarService,
    private message: NzMessageService
  ) {}

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      brand: [''],
      color: [''],
      transmission: [''],
      type: [''],
      radius: [null],
      startDate: [null, Validators.required],
      endDate: [null, Validators.required],
      sortBy: ['distance']
    });

    this.getUserLocation();
  }

  getUserLocation(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.searchCars(); // Initial fetch
      }, err => {
        alert('Location access denied. Please enable location.');
      });
    } else {
      alert('Geolocation not supported by your browser.');
    }
  }

 searchCars(): void {
  if (!this.latitude || !this.longitude) return;

  const filters = this.filterForm.value;

  // Convert dates to 'yyyy-MM-dd'
  filters.startDate = new Date(filters.startDate).toISOString().split('T')[0];
  filters.endDate = new Date(filters.endDate).toISOString().split('T')[0];

  this.loading = true;
  this.carService.searchCarsNearby(this.latitude, this.longitude, filters)
    .subscribe({
      next: (data: CarResponse[]) => {
        this.cars = data.map(car => ({
          ...car,
          processedImg: 'data:image/jpeg;base64,' + car.returnedImage
        }));
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Search error', err);
        this.loading = false;
      }
    });
}

onSubmit(): void {
  this.submitted = true;

  if (this.filterForm.invalid) {
    this.filterForm.markAllAsTouched(); 
    return;
  }

  this.searchCars();
}

}
