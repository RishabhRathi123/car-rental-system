import { Component } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent {
  cars: any[] = [];
  isSpinning = false;

  constructor(private adminService: AdminService,
              private message: NzMessageService) {}

  ngOnInit() {
    this.getAllCars();
  }

  getAllCars() {
    this.isSpinning = true;
    this.adminService.getAllCars().subscribe({
      next: (response) => {
        this.cars = response.map((element: any) => ({
          ...element,
          processedImg: 'data:image/jpeg;base64,' + element.returnedImage
        }));
        this.isSpinning = false;
      },
      error: () => {
        this.isSpinning = false;
        this.message.error('Failed to load cars.');
      }
    });
  }

  deleteCar(id: number) {
    this.isSpinning = true;
    this.adminService.deleteCar(id).subscribe({
      next: () => {
        this.cars = this.cars.filter(car => car.id !== id);
        this.message.success('Car deleted successfully!');
        this.isSpinning = false;
      },
      error: () => {
        this.message.error('Failed to delete car.');
        this.isSpinning = false;
      }
    });
  }

  softDelete(id: number) {
    this.isSpinning = true;
    this.adminService.softDeleteCar(id).subscribe({
      next: () => {
        this.cars = this.cars.filter(car => car.id !== id);
        this.message.success("Car soft-deleted successfully.");
        this.isSpinning = false;
      },
      error: () => {
        this.message.success("Refresh to see updated cars.");
        this.isSpinning = false;
      }
    });
  }
}
