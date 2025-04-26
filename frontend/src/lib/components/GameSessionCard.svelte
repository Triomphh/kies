<script>
  import PlayerSlot from './PlayerSlot.svelte';
  import SpectateButton from './SpectateButton.svelte';
  
  export let player1Name = "";
  export let player1Image = "https://placehold.co/88x88";
  export let player2Name = "";
  export let player2Image = "https://placehold.co/88x88";
  export let isFull = false;
  export let onJoin = () => {};
  export let onSpectate = () => {};
</script>

<div class="game-session-card {isFull ? 'full' : 'empty'}">
  <PlayerSlot seatTaken={true} playerName={player1Name} playerImage={player1Image} />
  
  <div class="vs-text-frame">
    <div class="vs"><span class="vs-span">VS</span></div>
  </div>
  
  {#if isFull}
    <PlayerSlot seatTaken={true} playerName={player2Name} playerImage={player2Image} />
    
    <div class="spectate-overlay">
      <SpectateButton onClick={onSpectate} />
    </div>
  {:else}
    <PlayerSlot seatTaken={false} playerName="" onClick={onJoin} />
  {/if}
</div>

<style>
  .game-session-card {
    width: 401px;
    height: 190px;
    padding-left: 47px;
    padding-right: 47px;
    border-radius: 13px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    position: relative;
  }
  
  .game-session-card:hover {
    border-radius: 0;
  }
  
  .game-session-card.empty:hover {
    outline: none;
    box-shadow: 0 4px 0 0 white;
  }
  
  .game-session-card.full:hover {
    outline: none;
  }
  
  .vs-text-frame {
    padding-bottom: 32px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 17px;
  }
  
  .vs {
    display: flex;
    justify-content: center;
    flex-direction: column;
  }
  
  .vs-span {
    color: #FFF;
    font-family: Sigmar;
    font-size: 32px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    word-wrap: break-word;
  }

  .spectate-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 10;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.1s;
  }
  
  .game-session-card.full:hover .spectate-overlay {
    opacity: 1;
    pointer-events: auto;
  }
</style>