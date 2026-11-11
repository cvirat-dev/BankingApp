import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-login',
  templateUrl: './auth-login.component.html',
  styleUrl: './auth-login.component.css'
})
export class AuthLoginComponent {

  private auth = inject(AuthService);
  private router = inject(Router);

  username: string = '';
  password: string = '';
  errorMessage: string = '';

  submit(): void {
    this.errorMessage = '';
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/konten']),
      error: (err) => this.errorMessage = err.status === 429 
        ? 'Too many attempts. Please try again later.' 
        : 'Login failed'
    });
  }

}
