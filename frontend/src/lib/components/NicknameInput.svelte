<script lang="ts">
  import { onMount } from 'svelte';
  import { playerService } from '$lib/services/playerService';
  import { authService } from '$lib/services/authService';
  import { browser } from '$app/environment';
  
  export let placeholder: string = "Pseudo...";
  export let value: string = "";
  
  let previousValue: string = "";
  let debounceTimer: ReturnType<typeof setTimeout>;
  let isLoading: boolean = false;
  let userId: string | null = null;
  let isAuthenticated: boolean = false;
  let isTemporaryPlayer: boolean = false;
  
  onMount(async () => {
    if (!browser) return;
    
    isAuthenticated = authService.isAuthenticated();
    isTemporaryPlayer = authService.isTemporaryPlayer();
    
    if (isAuthenticated) {
      const userInfo = authService.getUserInfo();
      userId = userInfo.id;
      value = userInfo.username || "";
    } else if (isTemporaryPlayer) {
      userId = localStorage.getItem('userId') || localStorage.getItem('tempPlayerId');
      value = localStorage.getItem('username') || localStorage.getItem('playerNickname') || "";
    } else {
      const storedUsername = localStorage.getItem('playerNickname');
      const storedUserId = localStorage.getItem('tempPlayerId');
      
      if (storedUsername && storedUserId) {
        value = storedUsername;
        userId = storedUserId;
      } else if (value && value.trim() !== '') {
        isLoading = true;
        try {
          const response = await authService.createTempPlayer({ nickname: value });
          userId = response.id.toString();
          localStorage.setItem('playerNickname', value);
          localStorage.setItem('tempPlayerId', userId);
        } catch (error) {
          console.error('Error creating temporary player:', error);
          value = generateRandomNickname();
        } finally {
          isLoading = false;
        }
      } else {
        value = generateRandomNickname();
      }
    }
    
    previousValue = value;
  });
  
  function generateRandomNickname(): string {
    const randomNum = Math.floor(Math.random() * 900) + 100;
    const nickname = `Anon${randomNum}`;
    createTempPlayerAsync(nickname);
    return nickname;
  }
  
  async function createTempPlayerAsync(nickname: string): Promise<void> {
    isLoading = true;
    try {
      const response = await authService.createTempPlayer({ nickname });
      userId = response.id.toString();
      localStorage.setItem('playerNickname', nickname);
      localStorage.setItem('tempPlayerId', userId);
    } catch (error) {
      console.error('Error creating temp player with random nickname:', error);
    } finally {
      isLoading = false;
    }
  }
  
  async function handleInput(event: Event) {
    const target = event.target as HTMLInputElement;
    value = target.value;
    
    clearTimeout(debounceTimer);
    
    debounceTimer = setTimeout(async () => {
      if (value !== previousValue && value.trim() !== '') {
        isLoading = true;
        
        try {
          isAuthenticated = authService.isAuthenticated();
          isTemporaryPlayer = authService.isTemporaryPlayer();
          
          if (isAuthenticated && userId) {
            await playerService.updateNickname(parseInt(userId), value);
            localStorage.setItem('username', value);
          } else if (isTemporaryPlayer && userId) {
            await playerService.updateNickname(parseInt(userId), value);
            localStorage.setItem('playerNickname', value);
            localStorage.setItem('username', value);
          } else if (userId) {
            try {
              await playerService.updateNickname(parseInt(userId), value);
              localStorage.setItem('playerNickname', value);
            } catch (e) {
              console.log("Update failed, creating new player");
              const response = await authService.createTempPlayer({ nickname: value });
              userId = response.id.toString();
              isTemporaryPlayer = true;
              localStorage.setItem('playerNickname', value);
              localStorage.setItem('tempPlayerId', userId);
              localStorage.setItem('username', value);
            }
          } else {
            console.log("Creating new temporary player");
            const response = await authService.createTempPlayer({ nickname: value });
            userId = response.id.toString();
            isTemporaryPlayer = true;
            
            localStorage.setItem('playerNickname', value);
            localStorage.setItem('tempPlayerId', userId);
            localStorage.setItem('username', value);
          }
          previousValue = value;
        } catch (error) {
          console.error('Error updating nickname:', error);
          value = previousValue;
        } finally {
          isLoading = false;
        }
      }
    }, 500);
  }
</script>

<input 
  type="text" 
  class="nickname-input" 
  {placeholder}
  bind:value
  on:input={handleInput}
  class:loading={isLoading}
/>

<style>
  .nickname-input {
    height: 45px;
    padding: 0 15px;
    background: white;
    border-radius: 6px;
    border: none;
    outline: 3px #E4E4E4 solid;
    outline-offset: -3px;
    box-sizing: border-box;
    
    color: black;
    font-size: 18px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    cursor: text;
    max-width: 180px;
    transition: outline-color 0.2s ease;
  }
  
  .nickname-input.loading {
    outline-color: #a0a0a0;
  }
  
  .nickname-input::placeholder {
    color: rgba(0, 0, 0, 0.25);
    font-size: 18px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    word-wrap: break-word;
  }
</style> 