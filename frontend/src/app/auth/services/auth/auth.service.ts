import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

const AUTH_API = environment.apiUrl + '/api/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}

  // ✅ Signup method (no Authorization header)
  register(signupRequest: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    const payload = {
      username: signupRequest.name,    // frontend field `name` maps to `username` in backend
      email: signupRequest.email,
      password: signupRequest.password,
      role: 'CUSTOMER'
    };

    return this.http.post(`${AUTH_API}/signup`, JSON.stringify(payload), { headers })
      .pipe(catchError(this.handleError));
  }

  login(loginRequest: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    const payload = {
      email: loginRequest.email,
      password: loginRequest.password
    };

    return this.http.post(`${AUTH_API}/login`, JSON.stringify(payload), { headers })
      .pipe(catchError(this.handleError));
  }

  // ✅ Error handler
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      // Backend error
      errorMessage = `Server returned code ${error.status}, body was: ${JSON.stringify(error.error)}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}
