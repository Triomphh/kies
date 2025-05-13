<script lang="ts">
  export let currentPlayerName: string | null = null;
  export let localPlayerId: string | null;
  export let currentPlayerId: string | null = null;

  $: isMyTurn = currentPlayerId !== null && localPlayerId !== null && currentPlayerId === localPlayerId;
  $: indicatorText = currentPlayerName 
    ? (isMyTurn ? `Your Turn (${currentPlayerName})` : `${currentPlayerName}'s Turn`)
    : 'Waiting for player...';
</script>

<div class="turn-indicator" class:my-turn={isMyTurn} class:opponent-turn={!isMyTurn && currentPlayerId !== null}>
  <p>{indicatorText}</p>
</div>

<style>
  .turn-indicator {
    padding: 10px 15px;
    border-radius: 6px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 15px;
    transition: background-color 0.3s ease, color 0.3s ease;
  }

  .turn-indicator p {
    margin: 0;
  }

  .turn-indicator {
    background-color: #e9ecef;
    color: #495057;
    border: 1px solid #ced4da;
  }

  .turn-indicator.my-turn {
    background-color: #28a745;
    color: white;
    border-color: #1e7e34;
  }

  .turn-indicator.opponent-turn {
    background-color: #ffc107;
    color: #212529;
    border-color: #e0a800;
  }
</style>