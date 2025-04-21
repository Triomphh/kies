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
  aria-label="Leaderboard button"
>
  <div class="classement"><span class="classement_span">Classement</span></div>
  <div data-svg-wrapper>
    <svg width="25" height="23" viewBox="0 0 25 23" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M5 22.75C4.64584 22.75 4.34917 22.63 4.11 22.39C3.87084 22.15 3.75084 21.8533 3.75 21.5C3.74917 21.1467 3.86917 20.85 4.11 20.61C4.35084 20.37 4.6475 20.25 5 20.25H20C20.3542 20.25 20.6513 20.37 20.8913 20.61C21.1313 20.85 21.2508 21.1467 21.25 21.5C21.2492 21.8533 21.1292 22.1504 20.89 22.3912C20.6508 22.6321 20.3542 22.7517 20 22.75H5ZM5.875 18.375C5.27084 18.375 4.73459 18.1771 4.26625 17.7812C3.79792 17.3854 3.51125 16.8854 3.40625 16.2812L2.15625 8.34375C2.11459 8.34375 2.06792 8.34916 2.01625 8.36C1.96459 8.37083 1.9175 8.37583 1.875 8.375C1.35417 8.375 0.911669 8.19291 0.547503 7.82875C0.183336 7.46458 0.000836174 7.02166 2.84091e-06 6.5C-0.000830492 5.97833 0.181669 5.53583 0.547503 5.1725C0.913336 4.80916 1.35584 4.62666 1.875 4.625C2.39417 4.62333 2.83709 4.80583 3.20375 5.1725C3.57042 5.53916 3.7525 5.98166 3.75 6.5C3.75 6.64583 3.73417 6.78125 3.7025 6.90625C3.67084 7.03125 3.63459 7.14583 3.59375 7.25L7.5 9L11.4063 3.65625C11.1771 3.48958 10.9896 3.27083 10.8438 3C10.6979 2.72916 10.625 2.4375 10.625 2.125C10.625 1.60416 10.8075 1.16125 11.1725 0.796245C11.5375 0.431245 11.98 0.249162 12.5 0.249995C13.02 0.250829 13.4629 0.433328 13.8288 0.797495C14.1946 1.16166 14.3767 1.60416 14.375 2.125C14.375 2.4375 14.3021 2.72916 14.1563 3C14.0104 3.27083 13.8229 3.48958 13.5938 3.65625L17.5 9L21.4063 7.25C21.3646 7.14583 21.3279 7.03125 21.2963 6.90625C21.2646 6.78125 21.2492 6.64583 21.25 6.5C21.25 5.97916 21.4325 5.53625 21.7975 5.17125C22.1625 4.80625 22.605 4.62416 23.125 4.625C23.645 4.62583 24.0879 4.80833 24.4538 5.1725C24.8196 5.53666 25.0017 5.97916 25 6.5C24.9983 7.02083 24.8163 7.46375 24.4538 7.82875C24.0913 8.19375 23.6483 8.37583 23.125 8.375C23.0833 8.375 23.0367 8.37 22.985 8.36C22.9333 8.35 22.8863 8.34458 22.8438 8.34375L21.5938 16.2812C21.4896 16.8854 21.2033 17.3854 20.735 17.7812C20.2667 18.1771 19.73 18.375 19.125 18.375H5.875ZM5.875 15.875H19.125L19.9375 10.6562L18.5 11.2812C17.9583 11.5104 17.4063 11.5521 16.8438 11.4062C16.2813 11.2604 15.8229 10.9479 15.4688 10.4687L12.5 6.375L9.53125 10.4687C9.17709 10.9479 8.71875 11.2604 8.15625 11.4062C7.59375 11.5521 7.04167 11.5104 6.5 11.2812L5.0625 10.6562L5.875 15.875Z" fill="#4C4C4C"/>
    </svg>
  </div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }
  
  .classement_span {
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

  .classement {
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
    box-shadow: 0px 4px 0px #BDDFFF inset;
    padding-top: 13px;
    padding-bottom: 5px;
  }
</style>