<script>
  export let onClick = () => {};
  export let isHovered = false;
</script>

<button 
  class="spectate-button {isHovered ? 'hovered' : 'default'}" 
  on:click={onClick}
  on:mouseenter={() => isHovered = true}
  on:mouseleave={() => isHovered = false}
>
  <div class="ok">
    <span class="ok-span">SPECTATE</span>
  </div>
</button>

<style>
  .spectate-button {
    padding: 4px 16px;
    background: #F1904A;
    border-radius: 8px;
    outline: 4px #DD762D solid;
    outline-offset: -4px;
    border: none;
    cursor: pointer;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: flex;
  }

  .spectate-button.hovered {
    background: #FC8C3B;
    outline: 4px #E87625 solid;
  }

  .ok {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .ok-span {
    color: white;
    font-size: 32px;
    font-family: 'Short Stack', cursive;
    font-weight: 400;
    word-wrap: break-word;
  }
</style>