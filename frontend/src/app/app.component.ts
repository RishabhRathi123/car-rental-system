import { Component } from '@angular/core';
import { StorageService } from './auth/services/storage/storage.service';
import { Router, NavigationEnd } from '@angular/router';
import { } from './NzZorroImportsModule'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'car-rental-angular';

  isCustomerLoggedIn=false;
  isAdminLoggedIn=false;
  
  constructor(private router: Router) {}
  
  ngOnInit(){
    this.router.events.subscribe((event) => {
      // Use instanceof (not constructor.name) so this still works after the
      // production build minifies/mangles class names.
      if(event instanceof NavigationEnd) {
      this.isCustomerLoggedIn = StorageService.isCustomerLoggedIn();
      this.isAdminLoggedIn = StorageService.isAdminLoggedIn();
    }
  })
  }

  get userName(): string {
    return StorageService.getUsername();
  }

  get userRole(): string {
    return StorageService.getUserRole();
  }

  logout() {
    StorageService.logout();
    this.router.navigateByUrl('/login');
  }
}
