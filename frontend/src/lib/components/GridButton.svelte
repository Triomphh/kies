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
  aria-label="Grid button"
>
  <div class="grille"><span class="grille_span">Grille</span></div>
  <div data-svg-wrapper>
    <svg width="28" height="27" viewBox="0 0 28 27" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M1.875 1.125V5.25M1.875 5.25V10.75C1.875 11.4793 2.16473 12.1788 2.68046 12.6945C3.19618 13.2103 3.89565 13.5 4.625 13.5H10.125M1.875 5.25H10.125M26.625 1.125V5.25M26.625 5.25V10.75C26.625 11.4793 26.3353 12.1788 25.8195 12.6945C25.3038 13.2103 24.6043 13.5 23.875 13.5H10.125M26.625 5.25H10.125M10.125 13.5V5.25M10.125 5.25V1.125M18.375 1.125V13.5M14.25 17.625V21.75M14.25 21.75V25.875M14.25 21.75H18.375M14.25 21.75H10.125" stroke="#4C4C4C" stroke-width="2.08333" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  </div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .grille_span {
    color: #4C4C4C;
    font-size: 24px;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    line-height: 1;
    word-wrap: break-word;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .grille {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
    height: 27px;
  }

  .statedefault {
    height: 45px;
    padding: 9px 16px;
    background: #FFF;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    justify-content: center;
    align-items: center;
    gap: 26px;
    display: inline-flex;
    flex-shrink: 0;
    cursor: pointer;
    box-sizing: border-box;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .statedefault:hover {
    background: #F4F4F4;
  }

  .statedefault:active {
    background: #F4F4F4;
    box-shadow: 0px 4px 0px #BDDFFF inset, 0px 4px 0px #F4F4F4;
    padding-top: 13px;
    padding-bottom: 5px;
  }
</style>