<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import { get } from 'svelte/store';
  import {
    gameState,
    gameId as activeGameIdStore,
    connectWebSocket,
    disconnectWebSocket,
    setPlayerReadyState,
    fetchGameState,
    addLogEntry
  } from '$lib/services/gameService';
  import { authStore } from '$lib/services/authService';
  import LeaveButton from '$lib/components/LeaveButton.svelte';
  import PlayerSlot from '$lib/components/PlayerSlot.svelte';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import GamePageLeaveDialog from '$lib/components/GamePageLeaveDialog.svelte';

  let gameIdParam: string | null = null;
  let currentUserPlayerId: string | null = null;
  let isCreator = false;
  let isOpponent = false;
  let showLobbyLeaveDialog = false;

  $: gameIdParam = $page.params.id;

  $: {
    const userInfo = $authStore;
    currentUserPlayerId = userInfo?.id?.toString() || null;
  }

  $: {
    if ($gameState && currentUserPlayerId) {
      isCreator = $gameState.playerOneId === currentUserPlayerId;
      isOpponent = $gameState.playerTwoId === currentUserPlayerId;
    } else {
      isCreator = false;
      isOpponent = false;
    }
  }
  
  $: {
    if ($gameState?.status === 'IN_PROGRESS' && $gameState?.gameId === gameIdParam) {
      console.log('[LobbyPage] Game status changed to IN_PROGRESS, navigating to game page.');
      goto(`/game/${$gameState.gameId}`);
    }
  }

  onMount(() => {
    if (gameIdParam) {
      console.log('[LobbyPage] Mounting lobby for game ID:', gameIdParam);
      activeGameIdStore.set(gameIdParam);
      connectWebSocket(gameIdParam);
      
      if (!get(gameState) || get(gameState)?.gameId !== gameIdParam) {
        fetchGameState(gameIdParam).then(initialState => {
          if (initialState) {
            console.log('[LobbyPage] Initial game state fetched:', initialState);
          } else {
            addLogEntry({type: 'event', message: 'Failed to fetch initial lobby state.', eventClass: 'error'});
          }
        });
      }
    } else {
      console.error('[LobbyPage] No game ID found in params.');
      goto('/game');
    }

    return () => {
      console.log('[LobbyPage] Unmounting lobby for game ID:', gameIdParam);
    };
  });
  
  onDestroy(() => {
      console.log('[LobbyPage] onDestroy hook for game ID:', gameIdParam);
  });


  async function handleReadyToggle() {
    if (!gameIdParam || !currentUserPlayerId) return;

    let currentReadyState = false;
    if (isCreator && $gameState) {
      currentReadyState = $gameState.creatorReady || false;
    } else if (isOpponent && $gameState) {
      currentReadyState = $gameState.opponentReady || false;
    }
    
    try {
      await setPlayerReadyState(gameIdParam, !currentReadyState);
    } catch (error: any) {
      console.error('[LobbyPage] Error setting player ready state:', error);
      addLogEntry({type: 'event', message: `Error setting ready: ${error.message || 'Unknown error'}`, eventClass: 'error'});
    }
  }

  async function handleLeaveGame() {
    showLobbyLeaveDialog = true;
  }

  async function handleConfirmLobbyLeave() {
    if (!gameIdParam) return;
    try {
        addLogEntry({type: 'event', message: 'You left the lobby.', eventClass: 'system'});
        disconnectWebSocket();
        activeGameIdStore.set(null);
        gameState.set(null);
        goto('/game');
    } catch (error: any) {
        console.error('[LobbyPage] Error confirming leave game:', error);
        addLogEntry({type: 'event', message: `Error leaving lobby: ${error.message || 'Unknown error'}`, eventClass: 'error'});
    } finally {
        showLobbyLeaveDialog = false;
    }
  }

  function handleCloseLobbyLeaveDialog() {
    showLobbyLeaveDialog = false;
  }

</script>

<BackgroundLayout>
<div class="lobby-container">
  {#if $gameState && $gameState.gameId === gameIdParam}
    <h1>Lobby de la partie</h1>

    <div class="players">
      <!-- Creator Slot -->
      <div class="player-slot">
        <h2>Joueur 1 (Créateur)</h2>
        {#if $gameState.creator}
          <p>{$gameState.creator.nickname}</p>
          <p class:text-green-400={$gameState.creatorReady} class:text-red-400={!$gameState.creatorReady}>
            {$gameState.creatorReady ? 'Prêt' : 'Pas Prêt'}
          </p>
          {#if isCreator}
            <button
              on:click={handleReadyToggle}
              class="mt-2 px-4 py-2 rounded text-white font-semibold
                     {$gameState.creatorReady ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'}">
              {$gameState.creatorReady ? 'Pas Prêt' : 'Prêt'}
            </button>
          {/if}
        {:else}
          <p>En attente du créateur...</p>
        {/if}
      </div>

      <!-- Opponent Slot -->
      <div class="player-slot">
        <h2>Joueur 2 (Adversaire)</h2>
        {#if $gameState.opponent}
          <p>{$gameState.opponent.nickname}</p>
          <p class:text-green-400={$gameState.opponentReady} class:text-red-400={!$gameState.opponentReady}>
            {$gameState.opponentReady ? 'Prêt' : 'Pas Prêt'}
          </p>
          {#if isOpponent}
            <button
              on:click={handleReadyToggle}
              class="mt-2 px-4 py-2 rounded text-white font-semibold
                     {$gameState.opponentReady ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'}">
              {$gameState.opponentReady ? 'Pas Prêt' : 'Prêt'}
            </button>
          {/if}
        {:else}
          <p class="text-gray-400">En attente de l'adversaire...</p>
        {/if}
      </div>
    </div>

    {#if $gameState && $gameState.creatorReady && $gameState.opponentReady && $gameState.status === 'WAITING'}
      <div class="countdown-container">
        <div class="countdown bg-green-500 text-white">
          <p class="text-2xl font-bold">Les deux joueurs sont prêts ! Démarrage de la partie...</p>
        </div>
      </div>
    {/if}
    
    <div class="actions text-center">
      <LeaveButton on:click={handleLeaveGame} />
    </div>

  {:else if !gameIdParam}
     <p class="text-red-500 text-center">Erreur : Aucun ID de partie spécifié.</p>
     <button on:click={() => goto('/game')} class="mt-4 px-4 py-2 bg-blue-500 hover:bg-blue-600 rounded text-white mx-auto block">
       Aller à la liste des parties
     </button>
  {:else}
    <p class="text-xl text-center text-gray-400">Chargement du lobby...</p>
  {/if}
</div>
<GamePageLeaveDialog
  isVisible={showLobbyLeaveDialog}
  on:leave={handleConfirmLobbyLeave}
  on:close={handleCloseLobbyLeaveDialog}
/>
</BackgroundLayout>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .lobby-container {
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Comic Neue', sans-serif;
  }

  h1 {
    font-size: 3.2rem;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    color: #333;
    text-align: center;
    margin-bottom: 1.5rem;
    text-shadow: 1px 1px 0 white;
  }

  h2 {
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    font-size: 1.8rem;
    color: #333;
  }

  .players {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  @media (max-width: 768px) {
    .players {
      grid-template-columns: 1fr;
    }
  }

  .player-slot {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
    align-items: center;
  }


  .player-slot p {
    font-size: 1.2rem;
    margin: 0.5rem 0;
    text-align: center;
  }

  .player-slot button {
    padding: 0.5rem 1.5rem;
    border-radius: 7px;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    font-size: 1.1rem;
    cursor: pointer;
    border: none;
    color: white;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    margin-top: 1rem;
  }


  .text-green-400 {
    color: #56BF72;
    font-weight: bold;
  }

  .text-red-400 {
    color: #EC4B4B;
    font-weight: bold;
  }

  .bg-green-500 {
    background: #56BF72;
  }

  .bg-green-500:hover {
    background: #4DAF67;
  }

  .bg-red-500 {
    background: #EC4B4B;
  }

  .bg-red-500:hover {
    background: #D74343;
  }

  .countdown-container {
     min-height: 60px;
     margin-bottom: 2rem;
     display: flex;
     justify-content: center;
     align-items: center;
  }
  
  .countdown {
    border-radius: 12px;
    padding: 1.2rem;
    text-align: center;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    width: 100%;
    max-width: 600px;
  }

  .bg-blue-500 {
    background: #4E97D1;
  }

  .bg-yellow-500 {
    background: #FFD166;
  }


  .actions {
    display: flex;
    justify-content: center;
    margin-top: 2rem;
  }

  button.mt-4 {
    padding: 0.5rem 1.5rem;
    border-radius: 7px;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    font-size: 1.1rem;
    cursor: pointer;
    border: none;
    color: white;
    background: #4E97D1;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
  }

  button.mt-4:hover {
    background: #3A87C1;
  }

</style>