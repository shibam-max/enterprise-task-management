import { api } from './api';

interface LoginCredentials {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
  user: {
    id: string;
    username: string;
    email: string;
    role: string;
  };
}

export const authService = {
  login: (credentials: LoginCredentials) => 
    api.post<LoginResponse>('/auth/login', credentials),
  
  register: (userData: any) => 
    api.post('/auth/register', userData),
  
  refreshToken: () => 
    api.post('/auth/refresh'),
  
  getCurrentUser: () => 
    api.get('/auth/me'),
};