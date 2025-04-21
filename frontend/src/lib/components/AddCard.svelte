<script lang="ts">
  export let hovered: boolean = false;
  export let onClick: () => void = () => {};
  let newCharacterName: string = '';

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      onClick();
    }
  }

  function handleCardKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }

  function stopPropagation(event: Event) {
    event.stopPropagation();
  }
</script>

<div 
  class="card" 
  class:hovered={hovered}
  on:mouseenter={() => hovered = true}
  on:mouseleave={() => hovered = false}
  on:click={onClick}
  on:keydown={handleCardKeyPress}
  role="button"
  tabindex="0"
  aria-label="Add a new character"
>
  <div data-svg-wrapper>
    <svg width="33" height="32" viewBox="0 0 33 32" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M2.5 16H30.5M16.5 2V30" stroke="black" stroke-opacity="0.41" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  </div>
  <div class="frame-5">
    <input 
      type="text" 
      class="character-name-input" 
      placeholder="Nom"
      bind:value={newCharacterName}
      on:click={stopPropagation}
      on:keydown={handleKeyPress}
    />
  </div>
</div>

<style>
  .card {
    display: inline-flex;
    width: 121px;
    height: 166px;
    padding: 5px 15px 5px 15px;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    gap: 44px;
    flex-shrink: 0;
    border-radius: 8px;
    background: rgba(196, 196, 196, 0.40);
    outline: 5px rgba(255, 255, 255, 0.40) solid;
    outline-offset: -5px;
    box-sizing: border-box;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
  }

  .card.hovered {
    background: rgba(196, 196, 196, 0.50);
    outline: 5px rgba(255, 255, 255, 0.50) solid;
  }

  .character-name-input {
    width: 76px;
    height: 21px;
    background: rgba(255, 255, 255, 0.70);
    border: none;
    text-align: center;
    font-family: 'Roboto Mono', monospace;
    font-size: 14px;
    font-weight: 500;
  }

  .character-name-input:focus {
    outline: none;
  }

  .frame-5 {
    align-self: stretch;
    height: 27px;
    padding-top: 6px;
    padding-left: 6px;
    padding-right: 6px;
    background: rgba(255, 255, 255, 0.40);
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: inline-flex;
  }

  [data-svg-wrapper] {
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>