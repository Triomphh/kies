<script>
  export let seatTaken = false;
  export let playerName = "";
  export let playerImage = "https://placehold.co/88x88";
  export let onClick = () => {};
</script>

{#if seatTaken}
  <div class="player-slot taken">
    <div class="avatar">
      <img class="avatar-image" src={playerImage} alt={playerName} />
    </div>
    <div class="player-name">
      <span class="name-text">{playerName}</span>
    </div>
  </div>
{:else}
  <button class="player-slot empty" on:click={onClick}>
    <div class="avatar">
      <svg xmlns="http://www.w3.org/2000/svg" width="88" height="88" viewBox="0 0 88 88" fill="none">
        <circle cx="44" cy="44" r="44" fill="white" fill-opacity="0.19"/>
        <circle cx="44" cy="44" r="42" stroke="white" stroke-opacity="0.21" stroke-width="4"/>
        <path d="M38 28H58V56C58 57.0609 57.5786 58.0783 56.8284 58.8284C56.0783 59.5786 55.0609 60 54 60H38M44 50L50 44M50 44L44 38M50 44H30" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
    <div class="button-text">
      <span class="text">ENTRER</span>
    </div>
  </button>
{/if}

<style>
  .player-slot {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 7px;
  }

  .player-slot.empty {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
  }

  .avatar {
    width: 88px;
    height: 88px;
    position: relative;
  }

  .avatar-image {
    width: 88px;
    height: 88px;
    border-radius: 9999px;
    border: 4px white solid;
    object-fit: cover;
  }

  .avatar svg {
    width: 88px;
    height: 88px;
    transition: opacity 0.2s;
  }

  .player-slot.empty:hover svg circle:first-child {
    fill-opacity: 0.34;
  }

  .player-slot.empty:hover svg circle:nth-child(2) {
    stroke-opacity: 0.42;
  }

  .player-name, .button-text {
    text-align: center;
    display: flex;
    flex-direction: column;
    max-width: 88px;
    max-height: 27px;
    justify-content: center;
    align-self: stretch;
  }

  .name-text, .text {
    color: white;
    font-size: 32px;
    font-family: Dongle, sans-serif;
    font-weight: 700;
    word-wrap: break-word;
  }
</style>