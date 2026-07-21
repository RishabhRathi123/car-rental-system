import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';

@Component({
  selector: 'app-post-car',
  templateUrl: './post-car.component.html',
  styleUrls: ['./post-car.component.scss'],
})
export class PostCarComponent {
  postCarForm!: FormGroup;
  isSpinning: boolean = false;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  listOfBrands = [
    "BMW", "AUDI", "FERRARI", "TESLA", "VOLVO", "TOYOTA", 
    "HONDA", "FORD", "NISSAN", "HYUNDAI", "LEXUS", "KIA"
  ];

  listOfType = ["Petrol", "Hybrid", "Diesel", "Electric", "CNG"];
  listOfColor = ["Red", "White", "Blue", "Black", "Orange", "Grey", "Silver"];
  listOfTransmission = ["Manual", "Automatic"];

  listOfCities: any[] = [];
  listOfCenters: any[] = [];

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private message: NzMessageService,
    private router: Router
  ) {}

  ngOnInit() {
    this.postCarForm = this.fb.group({
      name: [null, Validators.required],
      brand: [null, Validators.required],
      type: [null, Validators.required],
      color: [null, Validators.required],
      transmission: [null, Validators.required],
      price: [null, Validators.required],
      description: [null, Validators.required],
      year: [null, Validators.required],
      cityId: [null, Validators.required],
      centerId: [null, Validators.required]
    });

    // ✅ Load all cities here
    this.adminService.getCities().subscribe({
      next: (res) => {
        console.log("Cities loaded:", res); // Optional: Debug log
        this.listOfCities = res;
      },
      error: (err) => {
        console.error("Error loading cities:", err);
      }
    });

    // ✅ Load centers based on selected city
    this.postCarForm.get('cityId')?.valueChanges.subscribe(cityId => {
      if (cityId) {
        this.adminService.getCentersByCity(cityId).subscribe({
          next: (res) => {
            console.log("Centers loaded:", res); // Optional: Debug log
            this.listOfCenters = res;
          },
          error: (err) => {
            console.error("Error loading centers:", err);
            this.listOfCenters = [];
          }
        });
      } else {
        this.listOfCenters = [];
      }
    });
  }

  postCar() {
    console.log(this.postCarForm.value);
    this.isSpinning = true;

    const formData = new FormData();
    formData.append('name', this.postCarForm.value.name);
    formData.append('brand', this.postCarForm.value.brand);
    formData.append('type', this.postCarForm.value.type);
    formData.append('color', this.postCarForm.value.color);
    formData.append('transmission', this.postCarForm.value.transmission);
    formData.append('price', this.postCarForm.value.price);
    formData.append('description', this.postCarForm.value.description);
    formData.append('year', this.postCarForm.value.year);
    formData.append('centerId', this.postCarForm.value.centerId);

    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    this.adminService.postCar(formData).subscribe({
      next: (response) => {
        this.isSpinning = false;
        this.message.success('Car posted successfully!', { nzDuration: 5000 });
        this.router.navigateByUrl('/admin/dashboard');
      },
      error: (error) => {
        this.isSpinning = false;
        this.message.error('Failed to post car. Please try again.', { nzDuration: 5000 });
        console.error('Error posting car:', error);
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    this.previewImage();
  }

  previewImage() {
    if (!this.selectedFile) {
      this.imagePreview = null;
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(this.selectedFile);
  }
}
