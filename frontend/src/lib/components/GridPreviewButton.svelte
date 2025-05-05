<script lang="ts">
  import SmallCharacterCard from './SmallCharacterCard.svelte';
  
  export let gridName = "GRID NAME";
  export let author = "Jackie36";
  export let characters = [
    { name: "MACRON", imageUrl: "/images/macron.jpeg" },
    { name: "ANDRÉ", imageUrl: "/images/andre.png" },
    { name: "PABLO", imageUrl: "/images/cop.png" }
  ];
  export let isOfficial = false;
  
  let isHovered = false;
  
  function handleClick() {
    console.log(`Grid ${gridName} clicked`);
  }
  
  function handleMouseEnter() {
    isHovered = true;
  }
  
  function handleMouseLeave() {
    isHovered = false;
  }

  function handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      handleClick();
    }
  }
</script>

<div 
  class={isHovered ? "statehovered" : "statedefault"} 
  on:click={handleClick}
  on:keydown={handleKeyDown}
  on:mouseenter={handleMouseEnter}
  on:mouseleave={handleMouseLeave}
  role="button"
  tabindex="0"
  aria-label={`View grid: ${gridName} by ${author}`}
>
  <div class="cards_autolayout">
    <div class="card_left">
      <SmallCharacterCard characterName={characters[0].name} imageUrl={characters[0].imageUrl} />
    </div>
    <div class="card_center">
      <SmallCharacterCard characterName={characters[1].name} imageUrl={characters[1].imageUrl} />
    </div>
    <div class="card_right">
      <SmallCharacterCard characterName={characters[2].name} imageUrl={characters[2].imageUrl} />
    </div>
  </div>
  <div class="frame-21">
    <div class="grid-name"><span class="gridname_span">{gridName}</span></div>
    <div class="par-jackie36">
      {#if isOfficial}
        <span class="official-tag">OFFICIELLE</span>
      {:else}
        <span class="parjackie36_span">par {author}</span>
      {/if}
    </div>
  </div>
</div>

<style>
  .gridname_span {
    color: white;
    font-size: 30px;
    font-family: Roboto Mono;
    font-weight: 700;
    word-wrap: break-word;
  }

  .grid-name {
    width: 214px;
    max-height: 40px;
    justify-content: flex-start;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .official-tag {
    font-family: "Roboto Mono";
    font-size: 14px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    background: linear-gradient(86deg, rgba(144, 0, 255, 0.77) -2.12%, rgba(255, 204, 0, 0.77) 49.04%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .parjackie36_span {
    color: rgba(255, 255, 255, 0.77);
    font-size: 14px;
    font-family: Roboto Mono;
    font-weight: 700;
    word-wrap: break-word;
  }

  .par-jackie36 {
    width: 214px;
    max-height: 16px;
    justify-content: flex-start;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .frame-21 {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
  }

  .card_left {
    width: 85px;
    height: 116px;
    left: 0px;
    top: 33.27px;
    position: absolute;
    transform: rotate(-23deg);
    transform-origin: top left;
    z-index: 1;
    transition: z-index 0.01s linear;
    filter: drop-shadow(-2px 3px 0px rgba(0, 0, 0, 0.25));
  }

  .card_center {
    width: 85px;
    height: 116px;
    left: 64.17px;
    top: 0px;
    position: absolute;
    z-index: 2;
    transition: z-index 0.01s linear;
    filter: drop-shadow(0px 4px 0px rgba(0, 0, 0, 0.25));
  }

  .card_right {
    width: 85px;
    height: 116px;
    left: 141.27px;
    top: 0px;
    position: absolute;
    transform: rotate(26deg);
    transform-origin: top left;
    z-index: 3;
    transition: z-index 0.01s linear;
    filter: drop-shadow(2px 3px 0px rgba(0, 0, 0, 0.25));
  }
  
  .card_left:hover, .card_center:hover, .card_right:hover {
    z-index: 10;
  }

  .cards_autolayout {
    width: 217.07px;
    height: 141px;
    position: relative;
  }

  .statedefault {
    display: inline-flex;
    padding: 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    border-radius: 15px;
    cursor: pointer;
  }
  
  .statehovered {
    display: inline-flex;
    padding: 10px;
    background: rgba(255, 255, 255, 0.24);
    border-radius: 15px;
    outline: 5px rgba(255, 255, 255, 0.29) solid;
    outline-offset: -5px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    cursor: pointer;
  }
</style>