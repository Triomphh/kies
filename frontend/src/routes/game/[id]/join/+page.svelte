<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  import { authService } from '$lib/services/authService';
  import { getGameById, joinGame, type GameSession } from '$lib/services/gameService';
  
  let gameId: string = $page.params.id;
  let game: GameSession | null = null;
  let error: string | null = null;
  let isLoading = true;
  let isPasswordProtected = false;
  let password = '';
  let isJoining = false;
  
  onMount(async () => {
    if (!authService.isAuthenticated() && !authService.isTemporaryPlayer()) {
      goto('/login?redirect=' + encodeURIComponent(window.location.pathname));
      return;
    }
    
    await loadGame();
  });
  
  async function loadGame() {
    try {
      isLoading = true;
      error = null;
      
      const fetchedGame = await getGameById(parseInt(gameId));
      if (fetchedGame) {
        game = fetchedGame;
        isPasswordProtected = !!game.password;
      } else {
        error = "Partie non trouvée ou erreur lors du chargement.";
        isLoading = false;
        return;
      }
      
      const playerInfo = authService.getUserInfo();
      if (playerInfo && playerInfo.id && game) {
        const playerId = typeof playerInfo.id === 'string' ? parseInt(playerInfo.id, 10) : playerInfo.id;
        const isCreator = game.creator && game.creator.id === playerId;
        const isOpponent = game.opponent && game.opponent.id === playerId;
        
        if (isCreator || isOpponent) {
          goto(`/game/${gameId}`);
          return;
        }
      }
      
      if (game && game.opponent) {
        error = 'Cette partie est déjà complète. Vous pouvez essayer de la regarder en tant que spectateur.';
      }
    } catch (/** @type {unknown} */ err) {
      console.error('Error loading game:', err);
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      error = 'Erreur lors du chargement de la partie: ' + errorMessage;
    } finally {
      isLoading = false;
    }
  }
  
  async function handleJoinGame() {
    try {
      isJoining = true;
      error = null;
      
      await joinGame({
        gameId: parseInt(gameId),
        password: isPasswordProtected ? password : undefined,
        mode: 'play'
      });
      
      goto(`/game/${gameId}/lobby`);
    } catch (/** @type {unknown} */ err) {
      console.error('Error joining game:', err);
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      error = 'Erreur lors de la tentative de rejoindre la partie: ' + errorMessage;
      isJoining = false;
    }
  }
  
  async function handleSpectateGame() {
    try {
      isJoining = true;
      error = null;
      
      await joinGame({
        gameId: parseInt(gameId),
        password: isPasswordProtected ? password : undefined,
        mode: 'spectate'
      });
      
      goto(`/game/${gameId}`);
    } catch (/** @type {unknown} */ err) {
      console.error('Error spectating game:', err);
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      error = 'Erreur lors de la tentative de regarder la partie: ' + errorMessage;
      isJoining = false;
    }
  }
</script>

<BackgroundLayout>
  <div class="join-container">
    <Navbar 
      avatarUrl="https://placehold.co/88x88" 
      avatarAlt="User avatar" 
    />
    
    {#if isLoading}
      <div class="loading">Chargement de la partie...</div>
    {:else if error}
      <div class="error">
        <p>{error}</p>
        <button class="back-btn" on:click={() => goto('/')}>Retour à l'accueil</button>
        {#if game && game.opponent && game.allowSpectators}
          <button class="spectate-btn" on:click={handleSpectateGame}>
            Regarder en tant que spectateur
          </button>
        {/if}
      </div>
    {:else if game}
      <div class="join-panel">
        <h2>Rejoindre la partie</h2>
        
        <div class="game-info">
          <p><strong>Créateur :</strong> {game.creator?.nickname || 'Inconnu'}</p>
          <p><strong>Grille :</strong> {game.grid?.name || 'Inconnue'}</p>
          <p><strong>Manches :</strong> {game.maxRounds}</p>
          <p><strong>Tours par manche :</strong> {game.turnLimit || 'Illimité'}</p>
        </div>
        
        {#if isPasswordProtected}
          <div class="password-form">
            <label for="password">Mot de passe</label>
            <input 
              type="password" 
              id="password" 
              bind:value={password} 
              placeholder="Entrez le mot de passe de la partie"
              required
            />
          </div>
        {/if}
        
        <div class="action-buttons">
          <button 
            class="join-btn" 
            on:click={handleJoinGame}
            disabled={isJoining || (isPasswordProtected && !password)}
          >
            {isJoining ? 'Connexion...' : 'Rejoindre'}
          </button>
          
          <button class="cancel-btn" on:click={() => goto('/')}>
            Annuler
          </button>
        </div>
      </div>
    {:else}
      <div class="error">
        <p>Partie introuvable. Elle a peut-être été supprimée ou n'existe pas.</p>
        <button class="back-btn" on:click={() => goto('/')}>Retour à l'accueil</button>
      </div>
    {/if}
  </div>
</BackgroundLayout>

<style>
  .join-container {
    width: 100%;
    height: 100%;
    padding: 20px;
    box-sizing: border-box;
  }
  
  .loading, .error {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 100px;
    color: white;
    font-size: 18px;
    text-align: center;
  }
  
  .error {
    color: #ff6b6b;
  }
  
  .back-btn, .spectate-btn {
    margin-top: 20px;
    padding: 14px 24px;
    border: none;
    border-radius: 10px;
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: 0 4px 0 rgba(0, 0, 0, 0.1);
  }
  
  .back-btn:hover, .spectate-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 0 rgba(0, 0, 0, 0.1);
  }
  
  .back-btn:active, .spectate-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.1);
  }
  
  .back-btn {
    background-color: #4a6fa5;
  }
  
  .back-btn:hover {
    background-color: #3a5a85;
  }
  
  .spectate-btn {
    background-color: #6a1b9a;
    margin-left: 15px;
  }
  
  .spectate-btn:hover {
    background-color: #5c1888;
  }
  
  .join-panel {
    max-width: 500px;
    margin: 100px auto 0;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 13px;
    box-shadow: 0 6px 0 rgba(0, 0, 0, 0.15);
    padding: 35px;
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  .join-panel:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 0 rgba(0, 0, 0, 0.2);
  }
  
  h2 {
    text-align: center;
    margin-top: 0;
    margin-bottom: 25px;
    color: #333;
    font-size: 32px;
    font-family: 'Sigmar', Comic Neue, sans-serif;
    text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
  }
  
  .game-info {
    margin-bottom: 30px;
    background: rgba(245, 249, 252, 0.95);
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  }
  
  .game-info p {
    margin: 10px 0;
    color: #444;
    font-size: 16px;
    font-weight: 500;
  }
  
  .password-form {
    margin-bottom: 30px;
    background: rgba(245, 249, 252, 0.7);
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .password-form:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  label {
    display: block;
    margin-bottom: 10px;
    font-weight: 600;
    color: #333;
    font-size: 17px;
  }
  
  input {
    width: 100%;
    padding: 12px;
    border: 2px solid #ddeeff;
    border-radius: 8px;
    font-size: 16px;
    box-sizing: border-box;
    transition: border-color 0.3s, box-shadow 0.3s;
  }
  
  input:focus {
    border-color: #56BF72;
    outline: none;
    box-shadow: 0 0 0 3px rgba(86, 191, 114, 0.2);
  }
  
  .action-buttons {
    display: flex;
    justify-content: space-between;
  }
  
  .join-btn, .cancel-btn {
    padding: 14px 24px;
    border: none;
    border-radius: 10px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: 0 4px 0 rgba(0, 0, 0, 0.1);
  }
  
  .join-btn:hover, .cancel-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 0 rgba(0, 0, 0, 0.1);
  }
  
  .join-btn:active, .cancel-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.1);
  }
  
  .join-btn {
    background-color: #56BF72;
    color: white;
    flex-grow: 1;
    margin-right: 15px;
  }
  
  .join-btn:hover:not(:disabled) {
    background-color: #48a462;
  }
  
  .join-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
    box-shadow: none;
    opacity: 0.7;
  }
  
  .cancel-btn {
    background-color: #f5f5f5;
    color: #333;
    border: 2px solid #e0e0e0;
  }
  
  .cancel-btn:hover {
    background-color: #e0e0e0;
  }
</style>
