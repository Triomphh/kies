import { API_BASE_URL } from '$lib/config';
import { authService } from './authService';

// Types
export interface Character {
  characterId?: number;
  name: string;
  imageUrl: string;
  file?: File;
}

export interface Category {
  categoryId: number;
  name: string;
  creator: number;
}

export interface CreateGridRequest {
  name: string;
  categoryId: number;
  creatorId: number;
}

export interface CreateCategoryRequest {
  name: string;
  creatorId: number;
}

export interface AddCharacterRequest {
  name: string;
  imageUrl: string;
  creatorId: number;
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

const DEFAULT_CATEGORY_NAME = "General";

function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      if (typeof reader.result === 'string') {
        const base64 = reader.result.split(',')[1];
        resolve(base64);
      } else {
        reject(new Error('Failed to convert file to base64'));
      }
    };
    reader.onerror = error => reject(error);
  });
}

export const gridService = {
  async getAllGrids(): Promise<Grid[]> {
    try {
      const response = await fetch(`${API_BASE_URL}/grids`, {
        headers: authService.getAuthHeader()
      });
      
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
      const response = await fetch(`${API_BASE_URL}/grids/${id}`, {
        headers: authService.getAuthHeader()
      });
      
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
  },
  
  async createGrid(name: string, characters: Character[], categoryId?: number, overrideAccountId?: number): Promise<Grid> {
    try {
      if (!authService.isAuthenticated()) {
        throw new Error('Only authenticated users can create grids');
      }
      
      const userInfo = authService.getUserInfo();
      console.log('Creating grid with user info:', userInfo);
      
      if (!userInfo.id) {
        throw new Error('Cannot identify user ID');
      }
      
      let creatorId: number;
      
      if (overrideAccountId) {
        console.log('Using provided account ID:', overrideAccountId);
        creatorId = overrideAccountId;
      } else {
        const playerId = parseInt(userInfo.id);
        
        try {
          console.log(`Fetching account ID for player ID ${playerId}...`);
          const accountResponse = await fetch(`${API_BASE_URL}/accounts/player/${playerId}`, {
            headers: authService.getAuthHeader()
          });
          
          if (!accountResponse.ok) {
            throw new Error(`No account found for player ID ${playerId}`);
          }
          
          const accountData = await accountResponse.json();
          creatorId = accountData.accountId;
          console.log(`Using account ID ${creatorId} for player ID ${playerId}`);
        } catch (error) {
          console.error('Error getting account ID from player ID:', error);
          throw new Error('Failed to get account ID for this user');
        }
      }
      
      let finalCategoryId: number;
      
      if (categoryId) {
        finalCategoryId = typeof categoryId === 'string' ? parseInt(categoryId) : categoryId;
        console.log('Using provided category ID:', finalCategoryId);
      } else {
        try {
          const categoriesResponse = await fetch(`${API_BASE_URL}/categories`, {
            headers: authService.getAuthHeader()
          });
          
          if (!categoriesResponse.ok) {
            throw new Error('Failed to get categories');
          }
          
          const categories: Category[] = await categoriesResponse.json();
          
          if (categories && categories.length > 0) {
            finalCategoryId = categories[0].categoryId;
          } else {
            const createCategoryRequest: CreateCategoryRequest = {
              name: DEFAULT_CATEGORY_NAME,
              creatorId: parseInt(userInfo.id)
            };
            
            const createCategoryResponse = await fetch(`${API_BASE_URL}/categories`, {
              method: 'POST',
              headers: {
                ...authService.getAuthHeader(),
                'Content-Type': 'application/json'
              },
              body: JSON.stringify(createCategoryRequest)
            });
            
            if (!createCategoryResponse.ok) {
              throw new Error('Failed to create category');
            }
            
            const newCategory: Category = await createCategoryResponse.json();
            finalCategoryId = newCategory.categoryId;
          }
        } catch (error) {
          console.error('Error getting/creating category:', error);
          finalCategoryId = 1;
        }
      }
      
      const gridRequest: CreateGridRequest = {
        name,
        categoryId: finalCategoryId,
        creatorId: creatorId
      };
      
      console.log('Sending grid creation request:', JSON.stringify(gridRequest));
      
      const gridResponse = await fetch(`${API_BASE_URL}/grids`, {
        method: 'POST',
        headers: {
          ...authService.getAuthHeader(),
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(gridRequest)
      });
      
      if (!gridResponse.ok) {
        let errorMessage = `Error creating grid: ${gridResponse.status}`;
        try {
          const errorData = await gridResponse.json();
          console.error('Grid creation error response:', errorData);
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          }
        } catch (parseError) {
          console.error('Could not parse error response:', parseError);
        }
        throw new Error(errorMessage);
      }
      
      const grid = await gridResponse.json();
      const gridId = grid.gridId;
      
      for (const character of characters) {
        if (!character.name || !character.file) continue;
        
        let imageUrl = character.imageUrl || '';
        
        if (character.file) {
          try {
            const formData = new FormData();
            formData.append('file', character.file);
            
            const uploadResponse = await fetch(`${API_BASE_URL}/files/upload`, {
              method: 'POST',
              headers: authService.getAuthHeader(),
              body: formData
            });
            
            if (!uploadResponse.ok) {
              throw new Error(`Failed to upload image for character ${character.name}`);
            }
            
            const { imageUrl: uploadedUrl } = await uploadResponse.json();
            const backendBaseUrl = API_BASE_URL.replace('/api', '');
            imageUrl = `${backendBaseUrl}${uploadedUrl}`;
          } catch (error) {
            console.error('Image upload error:', error);
            const seed = character.name.toLowerCase().replace(/[^a-z0-9]/g, '');
            imageUrl = `https://api.dicebear.com/6.x/personas/png?seed=${seed}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf&size=128`;
          }
        } else if (!imageUrl || imageUrl.startsWith('blob:')) {
          const seed = character.name.toLowerCase().replace(/[^a-z0-9]/g, '');
          imageUrl = `https://api.dicebear.com/6.x/personas/png?seed=${seed}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf&size=128`;
        }
        
        const charRequest: AddCharacterRequest = {
          name: character.name,
          imageUrl: imageUrl,
          creatorId: creatorId
        };
        
        const charResponse = await fetch(`${API_BASE_URL}/grids/${gridId}/characters`, {
          method: 'POST',
          headers: {
            ...authService.getAuthHeader(),
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(charRequest)
        });
        
        if (!charResponse.ok) {
          throw new Error(`Failed to add character ${character.name}`);
        }
      }
      
      return await this.getGridById(gridId);
    } catch (error) {
      console.error('Failed to create grid:', error);
      throw error;
    }
  }
}; 