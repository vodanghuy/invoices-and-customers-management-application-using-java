import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstate';
import { State } from 'src/app/interface/state';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
  profileState$: Observable<State<CustomHttpResponse<Profile>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Profile>>(null);
  readonly DataState: DataState;

  constructor(private userService: UserService){}

  ngOnInit(): void{
    this.profileState$ = this.userService.profile$()
    .pipe(
      map(response => {
        console.log(response);
        this.dataSubject.next(response);
        return {dataState: DataState.LOADED, appData: response}
      }),
      startWith({dataState: DataState.LOADING}),
      catchError((error: string) => {
        return of ({dataState: DataState.ERROR, appData: this.dataSubject.value, error})
      })
    )
  }
}
