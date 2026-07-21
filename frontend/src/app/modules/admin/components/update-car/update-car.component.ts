import { Component } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { ActivatedRoute } from '@angular/router';
import { Form, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';


@Component({
  selector: 'app-update-car',
  templateUrl: './update-car.component.html',
  styleUrls: ['./update-car.component.scss']
})
export class UpdateCarComponent {
  carId: number=this.activatedRoute.snapshot.params['id'];
  imgChanged: boolean = false;
  selectedFile: any;
  imagePreview: string | ArrayBuffer | null = null;
  existingImage: string | null = null;
  updateForm!: FormGroup;
  listOfOption: Array<{ label: string; value: string }> = [];
  listOfBrands = [
  "BMW", "AUDI", "FERRARI", "TESLA", "VOLVO", "TOYOTA", 
  "HONDA", "FORD", "NISSAN", "HYUNDAI", "LEXUS", "KIA"
];

listOfType = [
  "Petrol", "Hybrid", "Diesel", "Electric", "CNG"
];

listOfColor = [
  "Red", "White", "Blue", "Black", "Orange", "Grey", "Silver"
];

listOfTransmission = [
  "Manual", "Automatic"
];
listOfCities: any[] = [];
listOfCenters: any[] = [];

isSpinning: boolean = false;

  constructor(private adminService: AdminService,
    private activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    private message: NzMessageService,
    private router: Router
  ) {}

  ngOnInit() {
    this.updateForm = this.fb.group({
      name: [null, Validators.required],
            brand: [null, Validators.required],
            type: [null, Validators.required],
            color: [null, Validators.required],
            transmission: [null, Validators.required],
            price: [null, Validators.required],
            description: [null, Validators.required],
            year: [null, Validators.required],
            cityId: [null, Validators.required],       // ✅ Add this
            centerId: [null, Validators.required] 
    });
    this.getCarById();
    // Load all cities
this.adminService.getCities().subscribe({
  next: (cities) => this.listOfCities = cities,
  error: (err) => console.error('Error loading cities:', err)
});

// Load centers when a city is selected
this.updateForm.get('cityId')?.valueChanges.subscribe(cityId => {
  if (cityId) {
    this.adminService.getCentersByCity(cityId).subscribe({
      next: (centers) => this.listOfCenters = centers,
      error: (err) => {
        console.error('Error loading centers:', err);
        this.listOfCenters = [];
      }
    });
  } else {
    this.listOfCenters = [];
  }
});

  }

  updateCar() {
  console.log(this.updateForm.value);
  this.isSpinning = true; // Start spinner
  const formData = new FormData();
  formData.append('name', this.updateForm.value.name);
  formData.append('brand', this.updateForm.value.brand);
  formData.append('type', this.updateForm.value.type);
  formData.append('color', this.updateForm.value.color);
  formData.append('transmission', this.updateForm.value.transmission);
  formData.append('price', this.updateForm.value.price);
  formData.append('description', this.updateForm.value.description);
  
  formData.append('year', this.updateForm.value.year);
  formData.append('cityId', this.updateForm.value.cityId);
  formData.append('centerId', this.updateForm.value.centerId);

  if (this.selectedFile) {
    formData.append('image', this.selectedFile); 
  }
  console.log(formData);
  this.adminService.updateCar(this.carId, formData).subscribe({
    next: (response) => {
      this.isSpinning = false; // Stop spinner
      this.message.success('Car updated successfully!', {nzDuration: 1000}); // For notifying Admin on updating Car successfully
      console.log(response);
      this.router.navigateByUrl('/admin/dashboard'); // Navigate to dashboard after successful update
    },
    error: (error) => {
      this.isSpinning = false; // Stop spinner
      this.message.error('Failed to update car. Please try again.', {nzDuration: 5000});
      console.error('Error updating car:', error);
    }
  }); 
  }

  getCarById() {
    this.isSpinning = true; // Start spinner
    this.adminService.getCarById(this.carId).subscribe({
      next: (response) => {
        // console.log('Car Details:', response);
        this.isSpinning = false; // Stop spinner
        const carDto = response;
        this.existingImage = 'data:image/jpeg;base64,' + response.returnedImage; 
        console.log('Car Details:', carDto);
        console.log('Processed Image:', this.existingImage);

        this.updateForm.patchValue(carDto);
        if (carDto.cityId) {
          this.adminService.getCentersByCity(carDto.cityId).subscribe({
            next: (centers) => this.listOfCenters = centers,
            error: (err) => console.error('Error loading centers by city:', err)
          });
        }
      },
      error: (error) => {
        console.error('Error fetching car details:', error);
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    this.imgChanged = true;
    this.existingImage = null; // Clear existing image when a new file is selected
    this.previewImage();
  }

  previewImage() {
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    }
    if (this.selectedFile) {
      reader.readAsDataURL(this.selectedFile);
    }
  }
}
