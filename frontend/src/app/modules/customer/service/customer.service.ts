import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from 'src/app/auth/services/storage/storage.service';
import { environment } from 'src/environments/environment';

const BASIC_URL = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private http: HttpClient) {}

  getAllCars(): Observable<any> {
    return this.http.get(BASIC_URL + '/api/customer/cars', {
      headers: this.createAuthorizationHeader()
    });
  }

  getCarById(carId: number): Observable<any> {
    return this.http.get(BASIC_URL + '/api/customer/car/' + carId, {
      headers: this.createAuthorizationHeader()
    });
  }

  bookACar(bookACarDto: any): Observable<any> {
    return this.http.post(BASIC_URL + '/api/customer/car/book', bookACarDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  deleteBooking(bookingId: number): Observable<any> {
    return this.http.delete(`${BASIC_URL}/api/customer/car/bookings/${bookingId}`, {
      headers: this.createAuthorizationHeader()
    });
  }

  getBookingsByUserId(): Observable<any> {
    return this.http.get(BASIC_URL + '/api/customer/car/bookings/' + StorageService.getUserId(), {
      headers: this.createAuthorizationHeader()
    });
  }

  searchCar(searchCarDto: any): Observable<any> {
    return this.http.post(BASIC_URL + '/api/customer/car/search', searchCarDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  createOrder(amount: number, receipt: string): Observable<any> {
    return this.http.post(BASIC_URL + '/api/payment/create-order', { amount, receipt }, {
      headers: this.createAuthorizationHeader()
    });
  }

  verifyPayment(data: any): Observable<any> {
    return this.http.post(BASIC_URL + '/api/payment/verify-payment', data, {
      headers: this.createAuthorizationHeader()
    });
  }

  createAuthorizationHeader(): HttpHeaders {
    const token = StorageService.getToken();
    let authHeaders: HttpHeaders = new HttpHeaders();
    return authHeaders.set('Authorization', 'Bearer ' + token);
  }
}
