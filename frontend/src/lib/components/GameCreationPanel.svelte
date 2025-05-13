<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { authService, authInitializedPromise, authStore, type AuthStoreState } from '$lib/services/authService';
  import { createGameAPI, getAvailableGrids, type Grid } from '$lib/services/gameService';
  
  let gridOptions: Array<{ value: string; label: string }> = [];
  let localIsLoading = true;
  let localError = '';
  let isPlayerReady = false;
  
  let selectedGrid = '';
  let maxRounds = 3;
  let turnLimit = 15;
  let isPasswordProtected = false;
  let password = '';
  let allowSpectators = true;
  
  $: {
    if ($authStore.isInitialized) {
      isPlayerReady = $authStore.isAuthenticated || $authStore.isTemporaryPlayer;
      if (!isPlayerReady && !localError) {
        localError = 'Vous devez avoir un identifiant de joueur (compte ou temporaire) pour créer une partie.';
      } else if (isPlayerReady) {
        if (localError === 'Vous devez avoir un identifiant de joueur (compte ou temporaire) pour créer une partie.') {
            localError = '';
        }
      }
    }
  }

  onMount(async () => {
    localIsLoading = true;
    await authInitializedPromise;

    if (isPlayerReady) {
      try {
        const grids: Grid[] = await getAvailableGrids();
        gridOptions = grids.map(grid => ({
          value: grid.gridId.toString(),
          label: `${grid.name} (${grid.characters.length} personnages)`
        }));
        if (gridOptions.length > 0) {
          selectedGrid = gridOptions[0].value;
        } else {
          if (!localError) localError = 'Aucune grille disponible. Créez une grille avant de commencer une partie.';
        }
      } catch (err) {
        console.error('Error loading grids:', err);
        if (!localError) localError = 'Erreur lors du chargement des grilles.';
      }
    }
    localIsLoading = false;
  });
  
  async function handleSubmit() {
    if (!isPlayerReady) {
      localError = 'Vous devez avoir un identifiant de joueur pour créer une partie.';
      return;
    }
    
    if (!selectedGrid) {
      localError = 'Vous devez sélectionner une grille.';
      return;
    }
    
    try {
      localError = '';
      localIsLoading = true;
      
      if (!$authStore.id) {
        localError = 'Impossible de récupérer l\'ID du joueur.';
        localIsLoading = false;
        return;
      }
      
      const creatorPlayerIdNum: number = $authStore.id;

      const gameData = {
        gridId: parseInt(selectedGrid),
        creatorPlayerId: creatorPlayerIdNum,
        maxRounds: maxRounds,
        turnLimit: turnLimit,
        password: isPasswordProtected ? password : undefined,
        allowSpectators: allowSpectators
      };
      
      const newGame = await createGameAPI(gameData);
      
      if (newGame && newGame.gameId) {
        goto(`/game/${newGame.gameId}/lobby`);
      } else {
        localError = 'Erreur lors de la création de la partie: ID de partie non reçu.';
        localIsLoading = false;
        return;
      }
    } catch (err: any) {
      console.error('Error creating game:', err);
      localError = err.message || 'Erreur lors de la création de la partie.';
    } finally {
      localIsLoading = false;
    }
  }

  $: isFormValid = isPlayerReady && selectedGrid && maxRounds > 0 && (!isPasswordProtected || password);
</script>

<div class="game-creation-panel">
  <h2>Créer une partie</h2>
  
  {#if localIsLoading && !$authStore.isInitialized}
    <div class="loading">Initialisation de l'authentification...</div>
  {:else if localIsLoading}
    <div class="loading">Chargement des données du panneau...</div>
  {:else if localError}
    <div class="error">{localError}</div>
  {:else if !isPlayerReady}
    <div class="error">Vous devez avoir un identifiant de joueur (compte ou temporaire) pour créer une partie.</div>
  {:else}
    <form on:submit|preventDefault={handleSubmit}>
      <div class="form-group">
        <label for="grid-select">Grille de jeu</label>
        <select id="grid-select" bind:value={selectedGrid} required>
          <option value="" disabled>Choisir une grille</option>
          {#each gridOptions as option}
            <option value={option.value}>{option.label}</option>
          {/each}
        </select>
        
        {#if gridOptions.length === 0}
          <div class="grid-warning">
            Vous n'avez pas encore de grille.
            <a href="/grids/add" class="create-grid-link">Créer une grille</a>
          </div>
        {/if}
      </div>
      
      <div class="form-group">
        <label for="max-rounds">Nombre de manches</label>
        <input 
          id="max-rounds" 
          type="number" 
          min="1" 
          max="10" 
          bind:value={maxRounds} 
          required
        />
      </div>
      
      <div class="form-group">
        <label for="turn-limit">Limite de tours par manche</label>
        <input 
          id="turn-limit" 
          type="number" 
          min="5" 
          max="50" 
          bind:value={turnLimit} 
          required
        />
      </div>
      
      <div class="form-group checkbox">
        <label>
          <input type="checkbox" bind:checked={isPasswordProtected} />
          Protéger par mot de passe
        </label>
      </div>
      
      {#if isPasswordProtected}
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input 
            id="password" 
            type="password" 
            bind:value={password} 
            required={isPasswordProtected}
          />
        </div>
      {/if}
      
      <div class="form-group checkbox">
        <label>
          <input type="checkbox" bind:checked={allowSpectators} />
          Autoriser les spectateurs
        </label>
      </div>
      
      <button 
        type="submit" 
        class="create-game-btn" 
        disabled={!isFormValid || localIsLoading}
      >
        {localIsLoading ? 'Création...' : 'Créer la partie'}
      </button>
    </form>
  {/if}
</div>

<style>
  .game-creation-panel {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      padding: 30px;
      box-sizing: border-box;
      overflow-y: auto;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 13px;
    }
    
    h2 {
      color: #333;
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 25px;
      text-align: center;
      font-family: 'Sigmar', Comic Neue, sans-serif;
      text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
    }
    
    .form-group {
      margin-bottom: 20px;
      background: rgba(245, 249, 252, 0.95);
      padding: 15px;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    }
    
    
    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #333;
      font-size: 17px;
    }
    
    .checkbox label {
      display: flex;
      align-items: center;
      font-weight: normal;
    }
    
    .checkbox input {
      margin-right: 10px;
      width: 18px;
      height: 18px;
    }
    
    input, select {
      width: 100%;
      padding: 12px;
      border: 2px solid #ddeeff;
      border-radius: 8px;
      font-size: 16px;
      background-color: #fff;
    }
    
    input:focus, select:focus {
      border-color: #56BF72;
      outline: none;
    }
    
    input[type="checkbox"] {
      width: auto;
    }
    
    .create-game-btn {
      width: 100%;
      padding: 15px;
      background-color: #56BF72;
      color: white;
      border: none;
      border-radius: 10px;
      font-size: 18px;
      font-weight: 700;
      cursor: pointer;
      margin-top: 15px;
      box-shadow: 0 4px 0 rgba(0, 0, 0, 0.1);
      position: relative;
      overflow: hidden;
    }
    
    .create-game-btn:hover:not(:disabled) {
      background-color: #48a462;
    }
    
    .create-game-btn:active:not(:disabled) {
      box-shadow: 0 2px 0 rgba(0, 0, 0, 0.1);
    }
    
    .create-game-btn:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
      box-shadow: none;
    }
    
    .loading, .error {
      padding: 20px;
      text-align: center;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      margin: 20px 0;
    }
    
    .loading {
      color: #56BF72;
      font-weight: 600;
    }
    
    .error {
      color: #d9534f;
      font-weight: 600;
    }
    
    .grid-warning {
      margin-top: 10px;
      background-color: rgba(255, 152, 0, 0.1);
      color: #ff9800;
      font-size: 15px;
      padding: 10px;
      border-radius: 8px;
      border-left: 3px solid #ff9800;
    }
    
    .create-grid-link {
      color: #2196F3;
      text-decoration: underline;
      cursor: pointer;
      font-weight: 600;
      display: inline-block;
      margin-left: 5px;
    }
    
    .create-grid-link:hover {
      color: #0d47a1;
    }
</style>
