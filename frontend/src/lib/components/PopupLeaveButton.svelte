<script lang="ts">
  export let onClick: () => void = () => {};

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }
</script>

<div 
  class="quitter_button"
  on:click={onClick}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-label="Leave button"
>
  <div class="quitter"><span class="quitter_span">Quitter</span></div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .quitter_span {
    color: white;
    font-size: 32px;
    font-family: 'Short Stack', cursive;
    font-weight: 400;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .quitter {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .quitter_button {
    width: 176px;
    height: 52px;
    padding: 10px;
    background: #EC4B4B;
    border-radius: 8px;
    outline: 4px #CA3D3D solid;
    outline-offset: -4px;
    justify-content: center;
    align-items: center;
    display: inline-flex;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .quitter_button:hover {
    background: #E74646;
    outline: 4px #D13737 solid;
  }

  .quitter_button:active {
    background: #E74646;
    transform: translateY(2px);
  }
</style>