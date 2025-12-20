import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CustomHttpResponse, Profile } from '../interface/appstate';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { State } from '../interface/state';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private readonly server = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  login$ = (email: string, password: string) => <Observable<CustomHttpResponse<Profile>>>
  this.http.post<CustomHttpResponse<Profile>>(`${this.server}/user/login`, {email, password})
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  );

  verifyCode$ = (email: string, code: string) => <Observable<CustomHttpResponse<Profile>>>
  this.http.get<CustomHttpResponse<Profile>>(`${this.server}/user/verify/code/${email}/${code}`)
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  );

  profile$ = () => <Observable<CustomHttpResponse<Profile>>>
  this.http.get<CustomHttpResponse<Profile>>
  (`${this.server}/user/profile`, {headers: new HttpHeaders().set('Authorization', 'Bearer')})
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  );

  handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: string;
    if(error.error instanceof ErrorEvent){
      errorMessage = `A client error occurred - ${error.error.message}`;
    }else{
      if(error.error.reason){
        errorMessage = error.error.reason;
      }else{
        errorMessage = `A server error occurred - Status: ${error.status}`;
      }
    }
    return throwError(() => errorMessage);
  }
}
