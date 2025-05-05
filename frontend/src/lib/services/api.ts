import { API_BASE_URL } from '$lib/config';

// Types
export interface Character {
  characterId: number;
  name: string;
  imageUrl: string;
}

export interface Grid {
  gridId: number;
  name: string;
  isOfficial: boolean;
  characters: Character[];
  creator: string;
  creatorId: number;
}

interface ApiError {
  error: string;
  message: string;
}

export const gridService = {
  async getAllGrids(): Promise<Grid[]> {
    try {
      const response = await fetch(`${API_BASE_URL}/grids`);
      
      if (!response.ok) {
        let errorData: ApiError;
        try {
          errorData = await response.json();
          throw new Error(errorData.message || `Error fetching grids: ${response.status}`);
        } catch (parseError) {
          throw new Error(`Error fetching grids: ${response.status}`);
        }
      }
      
      const data = await response.json();
      return Array.isArray(data) ? data : [];
    } catch (error) {
      console.error('Failed to fetch grids:', error);
      throw error;
    }
  },
  
  async getGridById(id: number): Promise<Grid> {
    try {
      const response = await fetch(`${API_BASE_URL}/grids/${id}`);
      
      if (!response.ok) {
        let errorData: ApiError;
        try {
          errorData = await response.json();
          throw new Error(errorData.message || `Error fetching grid: ${response.status}`);
        } catch (parseError) {
          throw new Error(`Error fetching grid: ${response.status}`);
        }
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Failed to fetch grid with ID ${id}:`, error);
      throw error;
    }
  }
}; 