// Config API
export const API_BASE_URL = import.meta.env.PUBLIC_API_URL 
  ? `${import.meta.env.PUBLIC_API_URL}/api`
  : 'http://localhost:8080/api';

// Settings App
export const APP_SETTINGS = {
  defaultAvatarUrl: 'https://placehold.co/88x88'
}; 