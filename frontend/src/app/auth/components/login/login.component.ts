import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';
import { StorageService } from '../../services/storage/storage.service';
import { timeout } from 'rxjs/operators';

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
    if (!this.loginForm.valid) {
      this.message.error('Please enter a valid email and password.');
      return;
    }

    this.isSpinning = true;

    this.authService.login(this.loginForm.value)
      .pipe(timeout(90000))
      .subscribe({
        next: (response: any) => {
          if (response && response.userId != null) {
            const user = {
              id: response.userId,
              role: response.userRole,
              username: response.userName
            };
            StorageService.saveUser(user);
            StorageService.saveToken(response.jwt);

            if (StorageService.isAdminLoggedIn()) {
              this.router.navigateByUrl('/admin/dashboard');
            } else if (StorageService.isCustomerLoggedIn()) {
              this.router.navigateByUrl('/customer/dashboard');
            } else {
              this.isSpinning = false;
              this.message.error('Login failed. Please try again.');
            }
          } else {
            this.isSpinning = false;
            this.message.error('Incorrect email or password.');
          }
        },
        error: (err) => {
          this.isSpinning = false;
          let msg = 'Login failed. Please try again.';
          if (err && err.name === 'TimeoutError') {
            msg = 'The server is taking too long (it may be waking up). Please try again in a moment.';
          } else if (err && (err.status === 401 || err.status === 403 || err.status === 404 || err.status === 500)) {
            msg = 'Incorrect email or password, or this account does not exist.';
          } else if (err && err.status === 0) {
            msg = 'Cannot reach the server. Check your connection and try again.';
          }
          this.message.error(msg, { nzDuration: 6000 });
        }
      });
  }

  customEmailValidator(control: FormControl): { [key: string]: boolean } | null {
    const email = control.value;
    if (email && !/(.com|.in)$/i.test(email)) {
      return { invalidDomain: true };
    }
    return null;
  }
}
