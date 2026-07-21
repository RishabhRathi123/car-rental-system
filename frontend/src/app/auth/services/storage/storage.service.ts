import { Injectable } from '@angular/core';


const TOKEN="token";
const USER="user";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }
  static saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN); // Clear any existing token
    window.localStorage.setItem(TOKEN, token);
  }

  static saveUser(user: any): void {
    window.localStorage.removeItem(USER); // Clear any existing user
    window.localStorage.setItem(USER, JSON.stringify(user));
  }

  static getUserId(): string {
    const user = this.getUser();
    if (user == null) return "";
    return user.id;
  }

  static getToken(){
    return window.localStorage.getItem(TOKEN);
  }
  static getUser(){
    const user = window.localStorage.getItem(USER);
    return user ? JSON.parse(user) : null;
  }
  static getUserRole(): string{
    const user = this.getUser();
    if (user==null) return "";
    return user.role;
  }
  static getUsername(): string {
    const user = this.getUser();
    if (user == null) return "";
    return user.username;
  }
  static isAdminLoggedIn(): boolean {
    if(this.getToken() == null) return false; // If no token, not logged in
    const role: string = this.getUserRole();
    return role === 'ADMIN';
  }
  static isCustomerLoggedIn(): boolean {
    if(this.getToken() == null) return false; // If no token, not logged in
    const role: string = this.getUserRole();
    return role === 'CUSTOMER';
  }

  static logout(): void {
    window.localStorage.removeItem(TOKEN);
    window.localStorage.removeItem(USER);
  }

}
