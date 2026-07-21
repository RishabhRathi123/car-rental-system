import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from 'src/app/auth/services/storage/storage.service';
import { environment } from 'src/environments/environment';

const BASIC_URL = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(
    private http: HttpClient
  ) {}

  postCar(carDto: any): Observable<any> {
    return this.http.post(
      BASIC_URL + '/api/admin/car',
      carDto,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  getAllCars(): Observable<any>{
    return this.http.get(
      BASIC_URL + '/api/admin/cars',
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  } 

  deleteCar(id: number): Observable<any> {
    return this.http.delete(
      BASIC_URL + '/api/admin/car/' + id,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }
  softDeleteCar(id: number): Observable<any> {
  return this.http.put(
    BASIC_URL + `/api/admin/car/delete/${id}`,
    {}, // empty body for PUT
    {
      headers: this.createAuthorizationHeader(),
    }
  );
}

  getCarById(id: number): Observable<any> {
    return this.http.get(
      BASIC_URL + '/api/admin/car/' + id,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

 updateCar(id: number, carDto: FormData): Observable<any> {
  const token = StorageService.getToken(); // Still fetch token
  return this.http.put(
    BASIC_URL + '/api/admin/car/' + id,
    carDto,
    {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + token
      })
    }
  );
}

getCarBookings(): Observable<any> {
    return this.http.get(
      BASIC_URL + '/api/admin/car/bookings',
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  changeCarBookingStatus(bookingId: number, status: string): Observable<any> {
    return this.http.get(
      BASIC_URL + `/api/admin/car/booking/${bookingId}/${status}`,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  searchCar(searchCarDto: any): Observable<any> {
    return this.http.post(
      BASIC_URL + '/api/admin/car/search',
      searchCarDto,
      {
        headers: this.createAuthorizationHeader(),
      }
    );
  }

  getCities(): Observable<any[]> {
  return this.http.get<any[]>(BASIC_URL+'/api/admin/cities',{
    headers: this.createAuthorizationHeader()
  });
}

getCentersByCity(cityId: number): Observable<any[]> {
  return this.http.get<any[]>(BASIC_URL+`/api/admin/centers/by-city/${cityId}`,
    {
      headers: this.createAuthorizationHeader()
    }
  );
}

  createAuthorizationHeader(): HttpHeaders {
    const token = StorageService.getToken(); // Use static method on class
    console.log('JWT Token:', token);
    let authHeaders: HttpHeaders = new HttpHeaders();
    return authHeaders.set('Authorization', 'Bearer ' + token);
  }
}
