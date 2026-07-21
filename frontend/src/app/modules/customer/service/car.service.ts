import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface CarResponse {
  id: number;
  brand: string;
  name: string;
  color: string;
  transmission: string;
  type: string;
  year: string;
  price: number;
  returnedImage: string;
  centerId: number;
  centerName: string;
  city: string;
  processedImg:string;
  description:string;
}


@Injectable({
  providedIn: 'root'
})
export class CarService {
  private apiUrl = environment.apiUrl + '/api/cars/search-nearby';

  constructor(private http: HttpClient) {}

  searchCarsNearby(lat: number, lng: number, filters: any): Observable<CarResponse[]> {
  let params = new HttpParams()
    .set('lat', lat)
    .set('lng', lng)
    .set('radius', filters.radius || '50');

  if (filters.brand) params = params.set('brand', filters.brand);
  if (filters.color) params = params.set('color', filters.color);
  if (filters.type) params = params.set('type', filters.type);
  if (filters.transmission) params = params.set('transmission', filters.transmission);
  if (filters.sortBy) params = params.set('sortBy', filters.sortBy);

  // ✅ Add startDate and endDate if available
  if (filters.startDate) params = params.set('startDate', filters.startDate);
  if (filters.endDate) params = params.set('endDate', filters.endDate);

  return this.http.get<CarResponse[]>(this.apiUrl, { params });
}

}
