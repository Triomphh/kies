<script lang="ts">
  import { goto } from '$app/navigation';
  import { browser } from '$app/environment';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import { authService, type RegisterRequest } from '$lib/services/authService';
  import { onMount } from 'svelte';
  
  let email = '';
  let nickname = '';
  let password = '';
  let confirmPassword = '';
  let age: number | null = null;
  let gender = 'MALE';
  let errorMessage = '';
  let isLoading = false;
  let isTemporaryPlayer = false;
  
  onMount(() => {
    if (browser) {
      if (authService.isAuthenticated()) {
        goto('/');
        return;
      }
      
      isTemporaryPlayer = authService.isTemporaryPlayer();
      if (isTemporaryPlayer) {
        nickname = localStorage.getItem('username') || localStorage.getItem('playerNickname') || '';
      }
    }
  });
  
  async function handleRegister() {
    errorMessage = '';
    
    if (!email || !nickname || !password || !confirmPassword || !age) {
      errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }
    
    if (!isValidEmail(email)) {
      errorMessage = 'Veuillez saisir une adresse email valide';
      return;
    }
    
    if (password !== confirmPassword) {
      errorMessage = 'Les mots de passe ne correspondent pas';
      return;
    }
    
    if (age < 1 || age > 120) {
      errorMessage = 'Veuillez saisir un âge valide';
      return;
    }
    
    isLoading = true;
    
    try {
      const registerData: RegisterRequest = {
        email,
        nickname,
        password,
        age: Number(age),
        gender
      };
      
      await authService.register(registerData);
      
      goto('/');
    } catch (error) {
      errorMessage = error instanceof Error ? error.message : 'Échec de l\'inscription. Veuillez réessayer.';
    } finally {
      isLoading = false;
    }
  }
  
  function goToLogin() {
    goto('/login');
  }
  
  function isValidEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }
</script>

<BackgroundLayout>
  <div class="register-container">
    <div class="register-card">
      <h1>CRÉER UN COMPTE</h1>
      
      {#if errorMessage}
        <div class="error-message">{errorMessage}</div>
      {/if}
      
      <form on:submit|preventDefault={handleRegister}>
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
        
        {#if !isTemporaryPlayer}
          <div class="form-group">
            <label for="nickname">PSEUDO</label>
            <input 
              type="text" 
              id="nickname" 
              bind:value={nickname} 
              placeholder="Entrez votre pseudo"
              required
            />
          </div>
        {/if}
        
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
        
        <div class="form-group">
          <label for="confirm-password">CONFIRMER LE MOT DE PASSE</label>
          <input 
            type="password" 
            id="confirm-password" 
            bind:value={confirmPassword} 
            placeholder="Confirmez votre mot de passe"
            required
          />
        </div>
        
        <div class="form-row">
          <div class="form-group age-group">
            <label for="age">ÂGE</label>
            <input 
              type="number" 
              id="age" 
              bind:value={age} 
              placeholder="Âge"
              min="1"
              max="120"
              required
            />
          </div>
          
          <div class="form-group gender-group">
            <label for="gender">GENRE</label>
            <select id="gender" bind:value={gender}>
              <option value="MALE">Homme</option>
              <option value="FEMALE">Femme</option>
              <option value="OTHER">Autre</option>
            </select>
          </div>
        </div>
        
        <button type="submit" class="register-button" disabled={isLoading}>
          {isLoading ? 'CRÉATION EN COURS...' : 'CRÉER UN COMPTE'}
        </button>
      </form>
      
      <div class="divider">
        <span>OU</span>
      </div>
      
      <button class="login-button" on:click={goToLogin}>
        CONNEXION
      </button>
    </div>
  </div>
</BackgroundLayout>

<style>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 20px;
  }
  
  .register-card {
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
    position: relative;
  }
  
  .form-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .age-group {
    flex: 1;
  }
  
  .gender-group {
    flex: 2;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
    color: #333;
    font-family: 'Comic Neue', sans-serif;
    font-size: 18px;
  }
  
  input, select {
    width: 100%;
    padding: 12px;
    border: 2px solid #ddd;
    border-radius: 13px;
    font-size: 16px;
    box-sizing: border-box;
    background-color: rgba(255, 255, 255, 0.9);
    transition: border-color 0.3s;
  }
  
  input:focus, select:focus {
    border-color: #56BF72;
    outline: none;
  }
  
  .register-button {
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
  
  .register-button:hover {
    background: #48a063;
    transform: translateY(-2px);
  }
  
  .register-button:active {
    transform: translateY(2px);
    box-shadow: 0px 2px 0px rgba(0, 0, 0, 0.25);
  }
  
  .register-button:disabled {
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
  
  .login-button {
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
  
  .login-button:hover {
    background: #a5c7eb;
    transform: translateY(-2px);
  }
  
  .login-button:active {
    transform: translateY(2px);
    box-shadow: 0px 2px 0px rgba(0, 0, 0, 0.25);
  }
</style> 