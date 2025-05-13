import { API_BASE_URL } from '$lib/config';
import { authService } from './authService';
import { browser } from '$app/environment';

export interface Player {
  playerId: number;
  nickname: string;
  gamesPlayed: number;
  victories: number;
}

export interface CreatePlayerRequest {
  nickname: string;
}

class PlayerService {
  async createPlayer(request: CreatePlayerRequest): Promise<Player> {
    try {
      const response = await fetch(`${API_BASE_URL}/players`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        throw new Error('Failed to create player');
      }

      return await response.json();
    } catch (error) {
      console.error('Error creating player:', error);
      throw error;
    }
  }

  async getPlayerById(id: number): Promise<Player> {
    try {
      const authHeaders = authService.getAuthHeader();
      const response = await fetch(`${API_BASE_URL}/players/${id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...authHeaders
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch player');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching player:', error);
      throw error;
    }
  }

  async updateNickname(playerId: number, nickname: string): Promise<Player> {
    try {
      const authHeaders = authService.getAuthHeader();
      const response = await fetch(`${API_BASE_URL}/players/${playerId}/nickname`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          ...authHeaders
        },
        body: JSON.stringify({ nickname })
      });

      if (!response.ok) {
        throw new Error('Failed to update nickname');
      }

      const updatedPlayer = await response.json();
      
      if (browser) {
        localStorage.setItem('username', updatedPlayer.nickname);
      }
      
      return updatedPlayer;
    } catch (error) {
      console.error('Error updating nickname:', error);
      throw error;
    }
  }

  async getLeaderboard(): Promise<Player[]> {
    try {
      const authHeaders = authService.getAuthHeader();
      const response = await fetch(`${API_BASE_URL}/players/leaderboard`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...authHeaders
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch leaderboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching leaderboard:', error);
      throw error;
    }
  }
}

export const playerService = new PlayerService(); 