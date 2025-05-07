<script lang="ts">
  import { goto } from '$app/navigation';
  import { browser } from '$app/environment';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import { authService, type LoginRequest } from '$lib/services/authService';
  import { onMount } from 'svelte';
  
  let email = '';
  let password = '';
  let errorMessage = '';
  let isLoading = false;
  let tempNickname = '';
  let isTemporaryPlayer = false;
  
  onMount(() => {
    if (browser) {
      if (authService.isAuthenticated()) {
        goto('/');
        return;
      }
      
      isTemporaryPlayer = authService.isTemporaryPlayer();
      if (isTemporaryPlayer) {
        tempNickname = localStorage.getItem('username') || localStorage.getItem('playerNickname') || '';
      }
    }
  });
  
  async function handleLogin() {
    errorMessage = '';
    
    if (!email || !password) {
      errorMessage = 'Veuillez saisir votre email et mot de passe';
      return;
    }
    
    if (!isValidEmail(email)) {
      errorMessage = 'Veuillez saisir une adresse email valide';
      return;
    }
    
    isLoading = true;
    
    try {
      const loginData: LoginRequest = {
        email,
        password
      };
      
      await authService.login(loginData);
      
      goto('/');
    } catch (error) {
      errorMessage = error instanceof Error ? error.message : 'Échec de la connexion. Veuillez réessayer.';
    } finally {
      isLoading = false;
    }
  }
  
  function goToRegister() {
    goto('/register');
  }
  
  function isValidEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }
</script>

<BackgroundLayout>
  <div class="login-container">
    <div class="login-card">
      <h1>CONNEXION</h1>
      
      {#if errorMessage}
        <div class="error-message">{errorMessage}</div>
      {/if}
      
      <form on:submit|preventDefault={handleLogin}>
        <div class="form-group">
          <label for="email">EMAIL</label>
          <input 
            type="email" 
            id="email" 
            bind:value={email} 
            placeholder="Entrez votre adresse email"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">MOT DE PASSE</label>
          <input 
            type="password" 
            id="password" 
            bind:value={password} 
            placeholder="Entrez votre mot de passe"
            required
          />
        </div>
        
        <button type="submit" class="login-button" disabled={isLoading}>
          {isLoading ? 'CONNEXION EN COURS...' : 'CONNEXION'}
        </button>
      </form>
      
      <div class="divider">
        <span>OU</span>
      </div>
      
      <button class="register-button" on:click={goToRegister}>
        CRÉER UN COMPTE
      </button>
    </div>
  </div>
</BackgroundLayout>

<style>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 20px;
  }
  
  .login-card {
    background: linear-gradient(0deg, white 0%, white 100%), 
                linear-gradient(133deg, rgba(255, 255, 157.79, 0.52) 0%, rgba(249.31, 142.29, 240.39, 0.19) 100%);
    border-radius: 13px;
    padding: 40px;
    width: 100%;
    max-width: 526px;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
    backdrop-filter: blur(5px);
    animation: fadeIn 0.5s ease-in-out;
  }
  
  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(-20px); }
    to { opacity: 1; transform: translateY(0); }
  }
  
  h1 {
    text-align: center;
    margin-bottom: 32px;
    color: #333;
    font-family: 'Sigmar', 'Comic Neue', sans-serif;
    font-size: 32px;
    letter-spacing: 1px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
    color: #333;
    font-family: 'Comic Neue', sans-serif;
    font-size: 18px;
  }
  
  input {
    width: 100%;
    padding: 12px;
    border: 2px solid #ddd;
    border-radius: 13px;
    font-size: 16px;
    box-sizing: border-box;
    background-color: rgba(255, 255, 255, 0.9);
    transition: border-color 0.3s;
  }
  
  input:focus {
    border-color: #56BF72;
    outline: none;
  }
  
  .login-button {
    width: 100%;
    padding: 14px;
    background: #56BF72;
    color: white;
    border: none;
    border-radius: 13px;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s;
    margin-top: 10px;
    font-family: 'Comic Neue', sans-serif;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
  }
  
  .login-button:hover {
    background: #48a063;
    transform: translateY(-2px);
  }
  
  .login-button:active {
    transform: translateY(2px);
    box-shadow: 0px 2px 0px rgba(0, 0, 0, 0.25);
  }
  
  .login-button:disabled {
    background: #9FA8DA;
    cursor: not-allowed;
    transform: none;
  }
  
  .error-message {
    background: #FFEBEE;
    color: #D32F2F;
    padding: 10px;
    border-radius: 13px;
    margin-bottom: 20px;
    text-align: center;
    font-family: 'Comic Neue', sans-serif;
    animation: shake 0.5s;
  }
  
  @keyframes shake {
    0%, 100% { transform: translateX(0); }
    10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
    20%, 40%, 60%, 80% { transform: translateX(5px); }
  }
  
  .divider {
    display: flex;
    align-items: center;
    margin: 25px 0;
    color: #555;
    font-family: 'Comic Neue', sans-serif;
  }
  
  .divider::before,
  .divider::after {
    content: "";
    flex: 1;
    border-bottom: 2px solid #ddd;
  }
  
  .divider span {
    padding: 0 10px;
    font-weight: bold;
    font-size: 18px;
    font-family: 'Sigmar', 'Comic Neue', sans-serif;
  }
  
  .register-button {
    width: 100%;
    padding: 14px;
    background: #BDDFFF;
    color: #333;
    border: none;
    border-radius: 13px;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s;
    font-family: 'Comic Neue', sans-serif;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
  }
  
  .register-button:hover {
    background: #a5c7eb;
    transform: translateY(-2px);
  }
  
  .register-button:active {
    transform: translateY(2px);
    box-shadow: 0px 2px 0px rgba(0, 0, 0, 0.25);
  }
</style> 