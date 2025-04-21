<script lang="ts">
  export let onClick: () => void = () => {};

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }
</script>

<div 
  class="buttonback-default"
  on:click={onClick}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-label="Back button"
>
  <div class="retour"><span class="retour-span">Retour</span></div>
  <div data-svg-wrapper>
    <svg width="23" height="21" viewBox="0 0 23 21" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path fill-rule="evenodd" clip-rule="evenodd" d="M10.6067 0.640165C10.171 0.640165 9.7531 0.813251 9.44501 1.12135C9.13691 1.42944 8.96383 1.84731 8.96383 2.28302C8.96383 2.71873 9.13691 3.1366 9.44501 3.4447C9.7531 3.75279 10.171 3.92588 10.6067 3.92588H15.0884C16.223 3.94797 17.3036 4.4142 18.0982 5.22439C18.8928 6.03459 19.3379 7.12412 19.3379 8.25891C19.3379 9.39371 18.8928 10.4832 18.0982 11.2934C17.3036 12.1036 16.223 12.5699 15.0884 12.592H7.76783V9.75309C7.76776 9.42822 7.67137 9.11066 7.49084 8.84055C7.31032 8.57045 7.05376 8.35994 6.75362 8.23562C6.45347 8.11131 6.1232 8.07877 5.80456 8.14213C5.48592 8.2055 5.19322 8.36191 4.96347 8.59159L0.485039 13.07C0.254321 13.2992 0.0968157 13.5917 0.0324746 13.9105C-0.0318664 14.2293 -0.000148486 14.56 0.123611 14.8607C0.203563 15.0579 0.322944 15.237 0.481754 15.398L4.96347 19.8797C5.19322 20.1094 5.48592 20.2658 5.80456 20.3291C6.1232 20.3925 6.45347 20.36 6.75362 20.2356C7.05376 20.1113 7.31032 19.9008 7.49084 19.6307C7.67137 19.3606 7.76776 19.043 7.76783 18.7182V15.8777H15.0884C16.0983 15.8925 17.1011 15.7064 18.0385 15.3302C18.9758 14.9539 19.829 14.3951 20.5484 13.6862C21.2679 12.9772 21.8391 12.1323 22.2291 11.2006C22.619 10.2689 22.8198 9.26895 22.8198 8.25891C22.8198 7.24888 22.619 6.24893 22.2291 5.31721C21.8391 4.38549 21.2679 3.54059 20.5484 2.83165C19.829 2.12272 18.9758 1.56389 18.0385 1.18766C17.1011 0.811442 16.0983 0.625334 15.0884 0.640165H10.6067Z" fill="white"/>
    </svg>
  </div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .retour-span {
    color: white;
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

  .retour {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
    height: 27px;
  }

  .buttonback-default {
    height: 45px;
    padding: 9px 16px;
    background: #F45E5E;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    justify-content: center;
    align-items: center;
    gap: 16px;
    display: inline-flex;
    cursor: pointer;
    box-sizing: border-box;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
  }

  .buttonback-default:hover {
    background: #F15858;
  }

  .buttonback-default:active {
    background: #F45E5E;
    box-shadow: 0px 4px 0px #BDDFFF inset, 0px 4px 0px #F45E5E;
    padding-top: 13px;
    padding-bottom: 5px;
  }
</style>