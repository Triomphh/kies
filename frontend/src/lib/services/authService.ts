import { API_BASE_URL } from '$lib/config';
import { browser } from '$app/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  nickname: string;
  password: string;
  age: number;
  gender: string;
  profileImageUrl?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  nickname: string;
  hasAccount: boolean;
  role: string;
}

export interface TempPlayerRequest {
  nickname: string;
}

export interface TempPlayerResponse {
  id: number;
  nickname: string;
}

class AuthService {
  private storeToken(response: AuthResponse): void {
    if (!browser) return;
    
    localStorage.setItem('authToken', response.token);
    localStorage.setItem('userId', response.id.toString());
    localStorage.setItem('username', response.nickname);
    localStorage.setItem('role', response.role);
    localStorage.setItem('hasAccount', response.hasAccount.toString());
  }

  isAuthenticated(): boolean {
    if (!browser) return false;
    
    const token = localStorage.getItem('authToken');
    if (!token) return false;
    
    const hasAccount = localStorage.getItem('hasAccount') === 'true';
    
    return hasAccount;
  }

  isTemporaryPlayer(): boolean {
    if (!browser) return false;
    
    const token = localStorage.getItem('authToken');
    const hasAccount = localStorage.getItem('hasAccount') === 'true';
    
    return token !== null && !hasAccount;
  }

  async login(loginRequest: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const authResponse = await response.json();
      this.storeToken(authResponse);
      return authResponse;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  async register(registerRequest: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerRequest)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Registration failed');
      }

      const authResponse = await response.json();
      this.storeToken(authResponse);
      return authResponse;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  async createTempPlayer(request: TempPlayerRequest): Promise<AuthResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/temp-player`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to create temporary player');
      }

      const authResponse = await response.json();
      this.storeToken(authResponse);
      return authResponse;
    } catch (error) {
      console.error('Create temp player error:', error);
      throw error;
    }
  }

  logout(): void {
    if (!browser) return;
    
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('hasAccount');
    
    localStorage.removeItem('tempPlayerId');
    localStorage.removeItem('playerNickname');
  }

  getUserInfo() {
    if (!browser) {
      return {
        id: null,
        username: '',
        role: ''
      };
    }
    
    return {
      id: localStorage.getItem('userId'),
      username: localStorage.getItem('username'),
      role: localStorage.getItem('role')
    };
  }

  getAuthHeader(): Record<string, string> {
    if (!browser) return {};
    
    const token = localStorage.getItem('authToken');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }
}

export const authService = new AuthService(); 