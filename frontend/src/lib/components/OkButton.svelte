<script lang="ts">
  export let onClick: () => void = () => {};

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }
</script>

<div 
  class="statedefault"
  on:click={onClick}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-label="OK button"
>
  <div class="ok"><span class="ok_span">OK</span></div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .ok_span {
    color: white;
    font-size: 40px;
    font-family: 'Short Stack', cursive;
    font-weight: 400;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .ok {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .statedefault {
    display: flex;
    width: 134px;
    height: 58px;
    padding: 4px 16px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    flex-shrink: 0;
    background: #7394E8;
    border-radius: 8px;
    outline: 4px #5F7ECF solid;
    outline-offset: -4px;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .statedefault:hover {
    background: #6B8BDC;
  }

  .statedefault:active {
    background: #6B8BDC;
    transform: translateY(2px);
  }
</style>