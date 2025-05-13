<script lang="ts">
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  import GameSessionCard from '$lib/components/GameSessionCard.svelte';
  import LeaderboardButton from '$lib/components/LeaderboardButton.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import GameCreationPanel from '$lib/components/GameCreationPanel.svelte';
  import { goto } from '$app/navigation';
  import { authService, authStore } from '$lib/services/authService';
  import {
    getAllGames,
    getGamesSummary,
    getGameById,
    type GameSession,
    type GameSummary
  } from '$lib/services/gameService';
  import { onMount, onDestroy } from 'svelte';
  import { derived } from 'svelte/store';

  interface GameSessionCardData {
    id: number;
    player1Name: string;
    player1Image: string;
    player2Name?: string;
    player2Image?: string;
    isFull: boolean;
    isPasswordProtected: boolean;
    allowSpectators: boolean;
  }
  
  let gameSessions: GameSessionCardData[] = [];
  let isLoading: boolean = true;
  let error: string | null = null;
  let gamesSummary: GameSummary[] = [];
  let refreshInterval: ReturnType<typeof setInterval> | null = null;

  const isAuthenticated = derived(authStore, $authStore => $authStore.isAuthenticated || $authStore.isTemporaryPlayer);
  const isAuthInitialized = derived(authStore, $authStore => $authStore.isInitialized);

  async function loadGames() {
    if (!$authStore.isInitialized) {
      return;
    }

    try {
      isLoading = true;
      error = null;
      
      if (!$isAuthenticated) {
        error = 'Veuillez vous identifier pour voir les parties disponibles';
        gameSessions = [];
        isLoading = false;
        return;
      }
      
      const games = await getAllGames();
      console.log('Loaded full game data:', games);
      
      if (games && Array.isArray(games)) {
        gameSessions = games.map(game => mapGameToCardData(game));
      } else {
        console.error('Invalid games data format:', games);
        gameSessions = [];
      }
      
      if ($isAuthenticated) {
        getGamesSummary().then(summary => {
          gamesSummary = summary;
          console.log('Primed gamesSummary cache after initial load:', gamesSummary);
        }).catch(err => console.error('Error priming game summaries cache:', err));
      }
    } catch (err) {
      console.error('Error loading games:', err);
      error = 'Impossible de charger les parties disponibles';
    } finally {
      isLoading = false;
    }
  }

  function mapGameToCardData(game: GameSession): GameSessionCardData {
    return {
      id: game.gameId,
      player1Name: game.creator.nickname,
      player1Image: game.creator.profileImageUrl || "/images/cop.png",
      player2Name: game.opponent?.nickname || "",
      player2Image: game.opponent?.profileImageUrl || "/images/andre.png",
      isFull: !!game.opponent,
      isPasswordProtected: !!game.password,
      allowSpectators: game.allowSpectators
    };
  }

  async function checkGamesAndUpdateList() {
    if (!$isAuthenticated || isLoading) return;

    try {
      const newSummaries = await getGamesSummary();

      const newSummariesMap = new Map(newSummaries.map(s => [s.gameId, s]));
      let changesMade = false;

      const currentSessionIds = new Set(gameSessions.map(gs => gs.id));

      for (const summary of newSummaries) {
        const existingSessionIndex = gameSessions.findIndex(gs => gs.id === summary.gameId);
        const oldSummaryData = gamesSummary.find(s => s.gameId === summary.gameId);

        if (existingSessionIndex === -1) {
          console.log(`Poll: New game detected with ID: ${summary.gameId}. Fetching details.`);
          const newGameDetails = await getGameById(summary.gameId);
          if (newGameDetails) {
            gameSessions = [...gameSessions, mapGameToCardData(newGameDetails)];
            changesMade = true;
          }
        } else {
          if (oldSummaryData && (
              oldSummaryData.hasOpponent !== summary.hasOpponent ||
              oldSummaryData.status !== summary.status ||
              oldSummaryData.spectatorCount !== summary.spectatorCount ||
              oldSummaryData.lastUpdated !== summary.lastUpdated
          )) {
            console.log(`Poll: Game ${summary.gameId} summary changed. Fetching details.`);
            const updatedGameDetails = await getGameById(summary.gameId);
            if (updatedGameDetails) {
              gameSessions[existingSessionIndex] = mapGameToCardData(updatedGameDetails);
              changesMade = true;
            }
          }
        }
        currentSessionIds.delete(summary.gameId);
      }

      if (currentSessionIds.size > 0) {
        console.log('Poll: Removing games no longer in summary:', Array.from(currentSessionIds));
        gameSessions = gameSessions.filter(gs => !currentSessionIds.has(gs.id));
        changesMade = true;
      }
      
      gamesSummary = newSummaries;

      if (changesMade) {
        gameSessions = [...gameSessions];
        console.log('Poll: gameSessions updated');
      }

    } catch (err) {
      console.error('Poll: Error checking game summaries:', err);
      if (err instanceof Error) {
        if (err.message && err.message.includes('401')) {
          if (err.message && err.message.includes('401') && $isAuthenticated) {
             error = 'Session expirée lors de la récupération des jeux. Veuillez vous reconnecter.';
          }
        }
      } else {
        console.error('Poll: An unexpected error object was thrown:', err);
      }
    }
  }

  let unsubscribeAuth: (() => void) | null = null;
  let unsubscribeIsInitialized: (() => void) | null = null;

  onMount(() => {
    unsubscribeIsInitialized = isAuthInitialized.subscribe(initialized => {
      if (initialized) {
        loadGames();
      }
    });

    unsubscribeAuth = isAuthenticated.subscribe(isAuthN => {
      if (isAuthN) {
        if (!refreshInterval) {
          checkGamesAndUpdateList();
          refreshInterval = setInterval(checkGamesAndUpdateList, 3000);
          console.log('User authenticated, starting poll interval.');
        }
      } else {
        if (refreshInterval) {
          clearInterval(refreshInterval);
          refreshInterval = null;
          console.log('User not authenticated, stopping poll interval.');
        }
        if ($authStore.isInitialized) {
            gameSessions = [];
            error = 'Veuillez vous identifier pour voir les parties disponibles';
        }
      }
    });

    return () => {
      if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
      }
      if (unsubscribeAuth) unsubscribeAuth();
      if (unsubscribeIsInitialized) unsubscribeIsInitialized();
    };
  });

  const handleJoinGame = (gameId: number) => {
    goto(`/game/${gameId}/join`);
  };
  
  const handleSpectateGame = (gameId: number) => {
    goto(`/game/${gameId}/spectate`);
  };
  
  const handleLeaderboardClick = () => {
    goto('/leaderboard');
  };
  
  const handleGridClick = () => {
    goto('/grids');
  };
  
  const customButtons = [
    {
      component: LeaderboardButton,
      props: { onClick: handleLeaderboardClick }
    },
    {
      component: GridButton,
      props: { onClick: handleGridClick }
    }
  ];
</script>

<BackgroundLayout>
  <div class="home-container">
    <Navbar 
      avatarUrl="https://placehold.co/88x88" 
      avatarAlt="User avatar" 
      customButtons={customButtons}
    />
    
    <div class="content-container">
      <div class="game-sessions-container">
        <div class="game-sessions-grid">
          {#if isLoading}
            <div class="loading">Chargement des parties disponibles...</div>
          {:else if !$authStore.isInitialized && isLoading}
            <div class="loading">Initialisation...</div>
          {:else if error && !$isAuthenticated && $authStore.isInitialized}
            <div class="error">{error}</div>
          {:else if gameSessions.length === 0 && $isAuthenticated && $authStore.isInitialized}
            <div class="no-games">Aucune partie disponible. Créez-en une !</div>
          {:else if !$isAuthenticated && $authStore.isInitialized}
             <div class="error">Veuillez vous identifier pour voir les parties disponibles.</div>
          {:else if $isAuthenticated && $authStore.isInitialized}
            {#each gameSessions.filter(s => !s.isFull) as session (session.id)}
              <GameSessionCard
                player1Name={session.player1Name}
                player1Image={session.player1Image} 
                isFull={false} 
                onJoin={() => handleJoinGame(session.id)} 
              />
            {/each}
            
            {#each gameSessions.filter(s => s.isFull && s.allowSpectators) as session (session.id)}
              <GameSessionCard 
                player1Name={session.player1Name} 
                player1Image={session.player1Image}
                player2Name={session.player2Name}
                player2Image={session.player2Image}
                isFull={true}
                onSpectate={() => handleSpectateGame(session.id)}
              />
            {/each}
          {/if}
        </div>
      </div>
      
      <div class="divider">
        <span>OU</span>
      </div>
      
      <div class="grid-info-panel">
        <GameCreationPanel />
      </div>
    </div>
  </div>
</BackgroundLayout>

<style>
  .home-container {
    width: 100%;
    height: 100%;
    padding: 20px;
    box-sizing: border-box;
  }
  
  .content-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 100px;
    gap: 48px;
  }
  
  .game-sessions-container {
    max-width: 956px;
  }
  
  .game-sessions-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 124px;
    justify-content: flex-start;
    align-content: flex-start;
    min-height: 100px;
  }
  
  .loading, .error, .no-games {
    width: 100%;
    padding: 20px;
    text-align: center;
    color: white;
    font-size: 18px;
    font-family: Comic Neue, sans-serif;
  }
  
  .error {
    color: #ff6b6b;
  }
  
  .divider {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    align-self: center;
  }
  
  .divider span {
    color: white;
    font-size: 32px;
    font-family: Comic Neue, sans-serif;
    font-weight: 700;
    text-align: center;
  }
  
  .grid-info-panel {
    width: 526px;
    height: 818px;
    background: linear-gradient(0deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.95) 100%),
                linear-gradient(133deg, rgba(86, 191, 114, 0.2) 0%, rgba(189, 223, 255, 0.4) 100%);
    box-shadow: 0px 6px 0px rgba(0, 0, 0, 0.15);
    border-radius: 13px;
    backdrop-filter: blur(5px);
    overflow: hidden;
  }
</style>
