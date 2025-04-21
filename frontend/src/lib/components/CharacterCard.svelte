<script lang="ts">
  export let characterName: string;
  export let imageUrl: string = "https://placehold.co/121x166";
  export let flipped: boolean = false;
  export let hovered: boolean = false;
  export let clicked: boolean = false;

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      clicked = !clicked;
    }
  }
</script>

<div 
  class="card" 
  class:flipped={flipped}
  class:hovered={hovered && !clicked}
  class:clicked={clicked}
  class:clicked-hovered={clicked && hovered}
  style="background-image: url({imageUrl});"
  on:mouseenter={() => hovered = true}
  on:mouseleave={() => hovered = false}
  on:click={() => clicked = !clicked}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-pressed={clicked}
  aria-label="Character card for {characterName}"
>
  {#if clicked && hovered}
    <div data-svg-wrapper>
      <svg width="35" height="32" viewBox="0 0 35 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M10.8333 1.66669L2.5 10L10.8333 18.3334M17.5 30H22.5C23.8132 30 25.1136 29.7414 26.3268 29.2388C27.5401 28.7363 28.6425 27.9997 29.5711 27.0711C30.4997 26.1425 31.2362 25.0401 31.7388 23.8269C32.2413 22.6136 32.5 21.3132 32.5 20C32.5 18.6868 32.2413 17.3864 31.7388 16.1732C31.2362 14.9599 30.4997 13.8575 29.5711 12.929C28.6425 12.0004 27.5401 11.2638 26.3268 10.7612C25.1136 10.2587 23.8132 10 22.5 10H4.16667" stroke="white" stroke-width="3.33333"/>
      </svg>
    </div>
  {/if}
  <div class="frame-5">
    <div class="andr"><span class="andr_span">{characterName}</span></div>
  </div>
</div>

<style>
  .andr_span {
    font-family: 'Roboto Mono', monospace;
    font-size: 14px;
    font-style: normal;
    font-weight: 500;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .andr {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .frame-5 {
    align-self: stretch;
    height: 27px;
    padding: 6px;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: inline-flex;
    width: 100%;
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.15);
  }
  
  .card {
    display: inline-flex;
    width: 121px;
    height: 166px;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    gap: 10px;
    flex-shrink: 0;
    border-radius: 8px;
    outline: 6.5px solid #FFF;
    outline-offset: -5px;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    box-sizing: border-box;
    padding: 5px 15px 5px 15px;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-color: lightgray;
    position: relative;
  }

  .card .andr_span {
    color: black;
  }

  .card .frame-5 {
    background: white;
  }

  .card.hovered {
    outline-color: #DD3033;
  }

  .card.hovered .andr_span {
    color: white;
  }

  .card.hovered .frame-5 {
    background: #DD3033;
  }

  .card.flipped {
    transform: rotateY(180deg);
    opacity: 0.6;
  }

  .card.clicked {
    outline-color: rgba(255, 255, 255, 0.15);
    box-shadow: none;
  }

  .card.clicked::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.7);
    z-index: 1;
    border-radius: 3px;
  }

  .card.clicked .andr_span {
    color: black;
    position: relative;
    z-index: 2;
  }

  .card.clicked .frame-5 {
    background: rgba(255, 255, 255, 0.15);
    position: relative;
    z-index: 2;
  }

  .card.clicked-hovered {
    gap: 37px;
  }

  [data-svg-wrapper] {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    z-index: 2;
  }
</style>