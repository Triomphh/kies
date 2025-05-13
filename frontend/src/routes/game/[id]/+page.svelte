<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';

  import PlayerGameBoard from '$lib/components/PlayerGameBoard.svelte';
  import AnswerInterface from '$lib/components/AnswerInterface.svelte';
  import GuessInterface from '$lib/components/GuessInterface.svelte';
  import GameHistoryLog from '$lib/components/GameHistoryLog.svelte';
  import TurnIndicator from '$lib/components/TurnIndicator.svelte';
  import GameStatusDisplay from '$lib/components/GameStatusDisplay.svelte';
  import GameChatWindow from '$lib/components/GameChatWindow.svelte';
  import type { DisplayMessage } from '$lib/types';

import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  import LeaveButton from '$lib/components/LeaveButton.svelte';
  import GamePageLeaveDialog from '$lib/components/GamePageLeaveDialog.svelte';

  import {
    gameId as currentGameIdStore,
    gameState,
    playerCharacters,
    opponentCharactersCount,
    gameLog,
    chatWindowMessages,
    gameStatusMessage,
    isConnected,
    currentTurnPlayerId,
    connectWebSocket,
    disconnectWebSocket,
    askQuestion,
    answerQuestion,
    makeGuess,
    flipCard,
    sendChatMessage,
    signalPassTurn,
    addLogEntry,
    fetchGameState,
  } from '$lib/services/gameService';
  import type { Character as GameCharacter, GameState } from '$lib/services/gameService';
  import { authStore } from '$lib/services/authService';


  let localPlayerId: string | null = null;
  let isGuessingMode = false;
  let showLeaveConfirmDialog = false;

  authStore.subscribe(value => {
    if (value.id) {
      localPlayerId = value.id.toString();
    }
  });

  let gameIdFromUrl: string;

  let availableCharactersForGuessInternal: Array<{ id: string; name: string; imageUrl?: string }> = [];
  let avatarUrl = "https://placehold.co/88x88";
  let avatarAlt = "Player avatar";
  function handleLeaveClick() {
    showLeaveConfirmDialog = true;
  }
 
  function handleConfirmLeave() {
    disconnectWebSocket();
    goto('/');
    showLeaveConfirmDialog = false;
  }
 
  function handleCloseLeaveDialog() {
    showLeaveConfirmDialog = false;
  }
 
  function handleAvatarClick() {
    console.log("Avatar clicked"); 
  }
  const customButtons = [{ component: LeaveButton, props: { onClick: handleLeaveClick } }];
let opponentCharacters: GameCharacter[] = [];



  onMount(() => {
    console.log('[+page.svelte] onMount started.');
    gameIdFromUrl = $page.params.id;
    console.log('[+page.svelte] gameIdFromUrl:', gameIdFromUrl);
    if (gameIdFromUrl) {
      currentGameIdStore.set(gameIdFromUrl);
      console.log('[+page.svelte] Calling connectWebSocket with gameId:', gameIdFromUrl);
      connectWebSocket(gameIdFromUrl);
    } else {
      gameStatusMessage.set("No game ID found. Cannot load game.");
      console.error("[+page.svelte] No game ID in URL");
    }

    return () => {
      console.log('[+page.svelte] onMount cleanup: disconnecting WebSocket.');
      disconnectWebSocket();
    };
  });

  isConnected.subscribe(value => {
    console.log('[+page.svelte] $isConnected changed to:', value);
  });

  gameState.subscribe(gs => {
    console.log('[+page.svelte] $gameState changed to:', gs);
    const currentAuthInfo = $authStore;

    if (gs && currentAuthInfo && currentAuthInfo.id !== null) {
      const localUserIdStr = currentAuthInfo.id.toString();
      console.log(`[+page.svelte] Processing gameState update. localUserId: ${localUserIdStr}, P1_ID: ${gs.playerOneId}, P2_ID: ${gs.playerTwoId}`);

      let pChars: GameCharacter[] = [];
      let oppCount = 0;

      if (gs.playerOneId && gs.playerOneId === localUserIdStr) {
        console.log('[+page.svelte] Player is P1. gs.playerOneGrid before assignment to pChars:', JSON.stringify(gs.playerOneGrid));
        pChars = gs.playerOneGrid || [];
        oppCount = gs.playerTwoGrid?.length || 0;
        opponentCharacters = gs.playerTwoGrid || [];
        console.log('[+page.svelte] Local player is Player 1. Characters (pChars):', pChars, 'Opponent count:', oppCount);
      } else if (gs.playerTwoId && gs.playerTwoId === localUserIdStr) {
        console.log('[+page.svelte] Player is P2. gs.playerTwoGrid before assignment to pChars:', JSON.stringify(gs.playerTwoGrid));
        pChars = gs.playerTwoGrid || [];
        oppCount = gs.playerOneGrid?.length || 0;
        opponentCharacters = gs.playerOneGrid || [];
        console.log('[+page.svelte] Local player is Player 2. Characters (pChars):', pChars, 'Opponent count:', oppCount);
      } else {
        console.log('[+page.svelte] Local player is not P1 or P2 (possibly spectator or IDs mismatch). Clearing character displays.');
        opponentCharacters = [];
      }
      
      playerCharacters.set(pChars);
      opponentCharactersCount.set(oppCount);

      if (pChars.length === 0 && (gs.playerOneId === localUserIdStr || gs.playerTwoId === localUserIdStr)) {
        console.warn(`[+page.svelte] playerCharacters is empty for an active player (P1_ID: ${gs.playerOneId}, P2_ID: ${gs.playerTwoId}, LocalUser_ID: ${localUserIdStr}). This might be okay if grids are not yet populated (e.g., pre-start or waiting for secret character selection).`);
      }

    } else {
      playerCharacters.set([]);
      opponentCharactersCount.set(0);
      opponentCharacters = [];
      if (!gs) {
        console.log('[+page.svelte] $gameState is null, clearing playerCharacters.');
      } else {
        console.log(`[+page.svelte] Auth info or ID not available (Auth ID: ${currentAuthInfo?.id}), or gameState not fully set. Clearing playerCharacters.`);
      }
    }
  });

  playerCharacters.subscribe(value => {
    console.log('[+page.svelte] $playerCharacters changed to:', value);
  });

  gameStatusMessage.subscribe(value => {
    console.log('[+page.svelte] $gameStatusMessage changed to:', value);
  });

  function handleFlipCardEvent(event: CustomEvent<{ characterId: string; characterName: string; isFlipped: boolean }>) {
    const { characterId, isFlipped } = event.detail;
    console.log('[+page.svelte] handleFlipCardEvent triggered. CharacterId:', characterId);
    if (!$currentGameIdStore || !localPlayerId) {
      console.warn('[+page.svelte] Cannot flip card: Game ID or Player ID missing.');
      return;
    }
    console.log('[+page.svelte] Calling flipCard for gameId:', $currentGameIdStore, 'characterId:', characterId);
    flipCard($currentGameIdStore, characterId, !isFlipped);
  }

  function handleGuessCharacterEvent(event: CustomEvent<{ characterId: string; characterName: string }>) {
    const { characterId } = event.detail;
    console.log('[+page.svelte] handleGuessCharacterEvent triggered. CharacterId:', characterId);
    if (!$currentGameIdStore || !localPlayerId) {
      console.warn('[+page.svelte] Cannot make guess: Game ID or Player ID missing.');
      return;
    }
    console.log('[+page.svelte] Calling makeGuess for gameId:', $currentGameIdStore, 'characterId:', characterId);
    makeGuess($currentGameIdStore, characterId);
    isGuessingMode = false;
    console.log('[+page.svelte] Exited guessing mode.');
  }

  function handleAskQuestion(event: CustomEvent<{ question: string }>) {
    if ($currentGameIdStore && event.detail.question) {
      askQuestion($currentGameIdStore, event.detail.question);
    }
  }
  
  function handleSubmitChatMessage(event: CustomEvent<{ text: string }>) {
    if ($currentGameIdStore && event.detail.text) {
      sendChatMessage($currentGameIdStore, event.detail.text);
    }
  }

  function handleAnswer(event: CustomEvent<{ answer: boolean }>) {
    const currentQuestionId = $gameState?.activeQuestionId;
    if ($currentGameIdStore && currentQuestionId) {
      answerQuestion($currentGameIdStore, currentQuestionId.toString(), event.detail.answer);
    } else {
      console.warn("Cannot answer: No active question ID found in game state or gameId missing.");
      addLogEntry({type: 'event', message: 'System: No active question to answer.', eventClass: 'error'});
    }
  }

  function handleChatAnswerSubmit(event: CustomEvent<{ questionId: string; answer: boolean }>) {
    if ($currentGameIdStore && event.detail.questionId) {
      answerQuestion($currentGameIdStore, event.detail.questionId, event.detail.answer);
    } else {
      console.warn("Cannot submit answer from chat: Game ID or Question ID missing.", $currentGameIdStore, event.detail.questionId);
      addLogEntry({type: 'event', message: 'System: Cannot submit answer. Invalid data.', eventClass: 'error'});
    }
  }

  function handleGuess(event: CustomEvent<{ characterId: string }>) {
    if ($currentGameIdStore && event.detail.characterId) {
      makeGuess($currentGameIdStore, event.detail.characterId);
    }
  }

  function enterGuessingMode(event: CustomEvent<{ questionId: string }>) {
    console.log('[+page.svelte] submitGuessRequest event received for questionId:', event.detail.questionId, 'Entering guessing mode.');
    isGuessingMode = true;
    console.log('[+page.svelte] Entering guessing mode. isGuessingMode set to true.');
    const guessPromptMsg = 'Vous avez choisi de deviner. Cliquez sur un personnage de votre grille pour faire votre proposition.';
    addLogEntry({type: 'event', message: guessPromptMsg, eventClass: 'system'});
    chatWindowMessages.update(msgs => [...msgs, {
      id: `guess_prompt_${Date.now()}`,
      timestamp: new Date(),
      type: 'event',
      text: guessPromptMsg,
      eventClass: 'system'
    }]);
  }

  function handlePassTurnFromChat(event: CustomEvent<{ questionId: string }>) {
    if ($currentGameIdStore) {
      console.log('[+page.svelte] submitPassTurn event received for questionId:', event.detail.questionId, 'Calling signalPassTurn.');
      signalPassTurn($currentGameIdStore);
      isGuessingMode = false;
    }
  }

  $: {
    if ($gameState && $gameState.playerTwoGrid) {
      availableCharactersForGuessInternal = $gameState.playerTwoGrid
        .filter(char => !char.isFlipped)
        .map(char => ({ id: char.id, name: char.characterName, imageUrl: char.imageUrl }));
    } else {
      availableCharactersForGuessInternal = [];
    }
  }

  $: amICurrentPlayer = !!($gameState?.status === 'IN_PROGRESS' && $currentTurnPlayerId && localPlayerId && $currentTurnPlayerId === localPlayerId);
  $: currentOpponentName = ($gameState?.playerOneId === localPlayerId ? $gameState?.playerTwoNickname : $gameState?.playerOneNickname) || "Opponent";

</script>

<BackgroundLayout>
  <div class="game-container">
    <div class="navbar-wrapper">
      <Navbar
        {avatarUrl}
        {avatarAlt}
        on:avatarClick={handleAvatarClick}
        {customButtons}
      />
    </div>
    

    <div class="game-content">
      <div class="game-grid-container">
        
        {#if $gameState}
          <GameStatusDisplay
            gameStatus={$gameState.status}
            currentRound={$gameState.currentRound}
            maxRounds={$gameState.maxRounds}
            creatorNickname={$gameState.playerOneNickname || $gameState.creator?.nickname}
            creatorRoundWins={$gameState.creatorRoundWins}
            opponentNickname={$gameState.playerTwoNickname || $gameState.opponent?.nickname}
            opponentRoundWins={$gameState.opponentRoundWins}
            statusMessage={$gameStatusMessage}
            messageType={$gameStatusMessage ? 'info' : ($gameState.status === 'WAITING' ? 'waiting' : 'info')}
          />
        {/if}

          {#if $playerCharacters && $playerCharacters.length > 0}
            <PlayerGameBoard
              characters={$playerCharacters}
              {isGuessingMode}
              on:flipCard={handleFlipCardEvent}
              on:guessCharacter={handleGuessCharacterEvent}
            />
          {:else if $gameState?.status === 'IN_PROGRESS' || $gameState?.status === 'WAITING'}
            <p class="waiting-text">Loading your characters or waiting for game to fully start...</p>
          {:else if !$isConnected}
            <p class="waiting-text">Connecting to game...</p>
          {:else}
            <p class="waiting-text">Game board will appear here.</p>
          {/if}
      </div>
      
      <div class="chat-container">
        <GameChatWindow
          messages={$chatWindowMessages}
          on:submitChatMessage={handleSubmitChatMessage}
          on:submitQuestion={handleAskQuestion}
          on:submitAnswer={handleChatAnswerSubmit}
          on:submitGuessRequest={enterGuessingMode}
          on:submitPassTurn={handlePassTurnFromChat}
          isInteractionAllowed={$gameState?.status === 'IN_PROGRESS'}
        />
      </div>
    </div>

  </div>

  <GamePageLeaveDialog
    isVisible={showLeaveConfirmDialog}
    on:leave={handleConfirmLeave}
    on:close={handleCloseLeaveDialog}
  />
</BackgroundLayout>

<style>
  .game-container {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    box-sizing: border-box;
    overflow-y: auto;
    max-height: 100vh;
  }
  
  .navbar-wrapper {
    width: 100%;
    height: 85px;
    margin-bottom: 20px;
  }
  
  .game-content {
    display: flex;
    gap: 21px;
    justify-content: center;
    align-items: flex-start;
    width: 100%;
    max-width: 1620px;
    margin: 0 auto;
    flex-grow: 1;
    overflow: visible;
    padding-bottom: 20px;
  }
  
  .game-grid-container {
    flex: 0 1 1073px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;
    max-width: 100%;
    overflow: visible;
  }
  
  
  .chat-container {
    flex: 0 0 700px;
    width: 700px;
    height: 710px;
    margin-top: 87px;
  }

  .game-interactions-new-location {
    width: 100%;
    max-width: 1073px;
    margin-top: 20px;
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f0f0f0;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    gap: 15px;
    box-sizing: border-box;
  }

  .waiting-text {
    font-style: italic;
    color: #555;
    text-align: center;
    padding: 10px;
  }
  
  .view-toggle-container {
    width: 100%;
    max-width: 1620px;
    display: flex;
    justify-content: flex-start;
    margin-bottom: 15px;
    padding: 0 10px;
    box-sizing: border-box;
  }
  
  .button-text-span {
    color: #4C4C4C;
    font-size: 24px;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    line-height: 1;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .button-text {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
    height: 27px;
  }

  .board-toggle-button {
    height: 45px;
    padding: 9px 16px;
    background: #FFF;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    justify-content: center;
    align-items: center;
    gap: 16px;
    display: inline-flex;
    flex-shrink: 0;
    cursor: pointer;
    box-sizing: border-box;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .board-toggle-button:hover {
    background: #F4F4F4;
  }

  .board-toggle-button:active {
    background: #F4F4F4;
    box-shadow: 0px 4px 0px #BDDFFF inset, 0px 4px 0px #F4F4F4;
    padding-top: 13px;
    padding-bottom: 5px;
  }
  
  @media (max-width: 1600px) {
    .game-content {
      flex-direction: column;
      align-items: center;
      padding: 0 10px;
    }
    
    .game-grid-container {
      flex: 0 1 auto;
      width: 100%;
      max-width: 95%;
      overflow-x: auto;
    }
        
    .chat-container {
      margin-top: 20px;
      flex: 0 0 auto;
      width: 100%;
      max-width: 600px;
      height: auto;
      min-height: 400px;
    }

    .game-interactions-new-location {
      width: 100%;
      max-width: 95%;
      margin-bottom: 20px;
    }
  }
  
  @media (max-width: 768px) {
    .game-container {
      padding: 10px;
    }
    
    .navbar-wrapper {
      margin-bottom: 10px;
    }
  }

  :global(body), :global(html) {
    margin: 0;
    padding: 0;
    height: 100%;
  }
  :global(.background-layout-slot-wrapper) {
    min-height: 100%;
    display: flex;
    overflow: visible;
    flex-direction: column;
  }
</style>
