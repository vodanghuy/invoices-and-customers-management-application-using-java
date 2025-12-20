import { DataState } from "../enum/datastate.enum";
import { Events } from "./event";
import { Role } from "./role";
import { User } from "./user";

export interface LoginState{
    dataState: DataState;
    loginSuccess?: boolean;
    error?: string;
    message?: string;
    isUsingMfa?: boolean;
    phone?: string;
}

export interface CustomHttpResponse<T> {
    timestamp: Date;
    status: string;
    statusCode: number;
    message?: string;
    reason?: string;
    developerMessage?: string;
    data?: T;
}

export interface Profile{
    user?: User;
    events?: Events[];
    role?: Role[];
    accessToken: string;
    refreshToken: string;
}