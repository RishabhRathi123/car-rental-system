import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {
  isSpinning = false;
  signupForm!: FormGroup;

  constructor(private fb: FormBuilder, private authService:AuthService, private message: NzMessageService, private router: Router) {}

  ngOnInit() {
    this.signupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [
    Validators.required,
    Validators.email,
    this.customEmailValidator  // custom validator
  ]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      checkPassword: ['', [Validators.required, this.confirmationValidate.bind(this)]]
    });

    // Re-validate confirm password if password changes
    this.signupForm.get('password')?.valueChanges.subscribe(() => {
      this.signupForm.get('checkPassword')?.updateValueAndValidity();
    })
  }

  register() {
    if (this.signupForm.valid) {
      console.log('Form submitted:', this.signupForm.value);
      this.authService.register(this.signupForm.value).subscribe((response: any) => { console.log(response);
        if(response.id!= null)
        {
          this.message.success('Registration successful! Please login.');
          this.signupForm.reset();
          this.router.navigateByUrl('/login');
        }
        else
        {
          this.message.error('Registration failed. Please try again.');
        }
       });
    } else {
      console.warn('Form is invalid');
    }
  }

  confirmationValidate(control: FormControl): { [key: string]: boolean } | null {
    if (!control.value) {
      return { required: true };
    }
    if (this.signupForm && control.value !== this.signupForm.get('password')?.value) {
      return { confirm: true, error: true };
    }
    return null;
  }

  customEmailValidator(control: FormControl): { [key: string]: boolean } | null {
  const email = control.value;
  if (email && !/(.com|.in)$/i.test(email)) {
    return { invalidDomain: true };
  }
  return null;
}
}


