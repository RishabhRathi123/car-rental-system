import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';
import { StorageService } from '../../services/storage/storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  isSpinning = false;
  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private message: NzMessageService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: ['', [
        Validators.required,
        Validators.email,
        this.customEmailValidator
      ]],
      password: ['', [Validators.required]]
    })
  }

  login() {
    if (this.loginForm.valid) {
      this.isSpinning = true;
      this.authService.login(this.loginForm.value).subscribe(
        (response: any) => {
            console.log(response);
            if(response.userId != null) {
              const user = {
                id: response.userId,
                role: response.userRole,
                username: response.userName
            }
            StorageService.saveUser(user);
            StorageService.saveToken(response.jwt);
            
            if(StorageService.isAdminLoggedIn()){
            this.router.navigateByUrl('/admin/dashboard');
            } else if(StorageService.isCustomerLoggedIn()){
              this.router.navigateByUrl('/customer/dashboard');
            }
            else {
              this.message.error('Login failed. Please try again.',{nzDuration: 50000});
            }
          }
        } 
      );
    } 
  }

  customEmailValidator(control: FormControl): { [key: string]: boolean } | null {
    const email = control.value;
    if (email && !/(.com|.in)$/i.test(email)) {
      return { invalidDomain: true };
    }
    return null;
  }
}
