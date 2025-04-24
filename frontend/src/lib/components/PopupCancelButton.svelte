<script lang="ts">
  export let onClick: () => void = () => {};

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }
</script>

<div 
  class="cancel_button"
  on:click={onClick}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-label="Cancel button"
>
  <div class="annuler"><span class="annuler_span">Annuler</span></div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .annuler_span {
    color: #494949;
    font-size: 32px;
    font-family: 'Short Stack', cursive;
    font-weight: 400;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .annuler {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .cancel_button {
    display: flex;
    width: 176px;
    height: 52px;
    padding: 19px 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    flex-shrink: 0;
    border-radius: 8px;
    border: 4px solid #E0E0E0;
    background: #f3f3f3;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .cancel_button:hover {
    border-radius: 8px;
    border: 4px solid #DDD;
    background: #EFEFEF;
  }

  .cancel_button:active {
    background: #EFEFEF;
    transform: translateY(2px);
  }
</style>