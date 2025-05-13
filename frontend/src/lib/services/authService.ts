import { API_BASE_URL } from '$lib/config';
import { browser } from '$app/environment';
import { writable, get } from 'svelte/store';

export interface AuthStoreState {
  isAuthenticated: boolean;
  isTemporaryPlayer: boolean;
  id: number | null;
  nickname: string;
  role: string;
  isInitialized: boolean;
}

const initialAuthState: AuthStoreState = {
  isAuthenticated: false,
  isTemporaryPlayer: false,
  id: null,
  nickname: '',
  role: '',
  isInitialized: false,
};

export const authStore = writable<AuthStoreState>(initialAuthState);

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
  private generateAnonymousNickname(): string {
    const randomId = Math.floor(Math.random() * 90000) + 10000;
    return `Anon${randomId}`;
  }

  public async initialize(): Promise<void> {
    if (!browser) {
      authStore.set({ ...initialAuthState, isInitialized: true });
      return;
    }

    const token = localStorage.getItem('authToken');
    const userIdString = localStorage.getItem('userId');
    const username = localStorage.getItem('username') || '';
    const role = localStorage.getItem('role') || '';
    const hasAccountString = localStorage.getItem('hasAccount');

    if (token && userIdString) {
      const userId = parseInt(userIdString, 10);
      const hasAccount = hasAccountString === 'true';

      authStore.set({
        isAuthenticated: hasAccount,
        isTemporaryPlayer: !hasAccount,
        id: userId,
        nickname: username,
        role: role,
        isInitialized: true,
      });
    } else {
      try {
        const tempNickname = this.generateAnonymousNickname();
        const tempPlayerRequest: TempPlayerRequest = { nickname: tempNickname };
        await this.createTempPlayer(tempPlayerRequest);
      } catch (error) {
        console.error('Failed to create temporary player during initialization:', error);
        authStore.set({
          ...initialAuthState,
          isInitialized: true,
        });
      }
    }
  }

  private storeToken(response: AuthResponse): void {
    if (!browser) return;

    localStorage.setItem('authToken', response.token);
    localStorage.setItem('userId', response.id.toString());
    localStorage.setItem('username', response.nickname);
    localStorage.setItem('role', response.role);
    localStorage.setItem('hasAccount', response.hasAccount.toString());

    authStore.set({
      isAuthenticated: response.hasAccount,
      isTemporaryPlayer: !response.hasAccount,
      id: response.id,
      nickname: response.nickname,
      role: response.role,
      isInitialized: true,
    });
  }

  isAuthenticated(): boolean {
    if (!browser) return false;
    return get(authStore).isAuthenticated;
  }

  isTemporaryPlayer(): boolean {
    if (!browser) return false;
    return get(authStore).isTemporaryPlayer;
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

    authStore.set({
      ...initialAuthState,
      isInitialized: true,
    });
  }

  getUserInfo() {
    if (!browser) {
      return { id: null, username: '', role: '' };
    }
    const storeState = get(authStore);
    return {
      id: storeState.id,
      username: storeState.nickname,
      role: storeState.role,
    };
  }

  getAuthHeader(): Record<string, string> {
    if (!browser) return {};
    
    const token = localStorage.getItem('authToken');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  getRawJwtToken(): string | null {
    if (!browser) return null;
    return localStorage.getItem('authToken');
  }
}

export const authService = new AuthService();
export const authInitializedPromise = authService.initialize();