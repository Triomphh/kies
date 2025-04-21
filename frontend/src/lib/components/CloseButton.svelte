<script lang="ts">
  export let onClick: () => void = () => {};

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      onClick();
    }
  }
</script>

<div 
  class="buttonclose"
  on:click={onClick}
  on:keydown={handleKeyPress}
  role="button"
  tabindex="0"
  aria-label="Close button"
>
  <div data-svg-wrapper class="property-1default">
    <svg width="35" height="35" viewBox="0 0 35 35" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="1.5" y="1.5" width="32" height="32" rx="3.5" fill="#DA6767" stroke="#A75A5A" stroke-width="3"/>
    <path d="M9.47523 9.47523C9.77962 9.17094 10.1924 9 10.6228 9C11.0532 9 11.466 9.17094 11.7704 9.47523L17.5098 15.2147L23.2492 9.47523C23.5554 9.17957 23.9654 9.01596 24.391 9.01966C24.8165 9.02336 25.2236 9.19406 25.5246 9.49501C25.8255 9.79595 25.9962 10.2031 25.9999 10.6286C26.0036 11.0542 25.84 11.4642 25.5444 11.7704L19.8049 17.5098L25.5444 23.2492C25.84 23.5554 26.0036 23.9654 25.9999 24.391C25.9962 24.8165 25.8255 25.2236 25.5246 25.5246C25.2236 25.8255 24.8165 25.9962 24.391 25.9999C23.9654 26.0036 23.5554 25.84 23.2492 25.5444L17.5098 19.8049L11.7704 25.5444C11.4642 25.84 11.0542 26.0036 10.6286 25.9999C10.2031 25.9962 9.79595 25.8255 9.49501 25.5246C9.19406 25.2236 9.02336 24.8165 9.01966 24.391C9.01596 23.9654 9.17957 23.5554 9.47523 23.2492L15.2147 17.5098L9.47523 11.7704C9.17094 11.466 9 11.0532 9 10.6228C9 10.1924 9.17094 9.77962 9.47523 9.47523Z" fill="white"/>
    </svg>
  </div>
  <div data-svg-wrapper class="property-1hovered">
    <svg width="35" height="35" viewBox="0 0 35 35" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="1.5" y="1.5" width="32" height="32" rx="3.5" fill="#CA6262" stroke="#A75A5A" stroke-width="3"/>
    <path d="M9.47523 9.47523C9.77962 9.17094 10.1924 9 10.6228 9C11.0532 9 11.466 9.17094 11.7704 9.47523L17.5098 15.2147L23.2492 9.47523C23.5554 9.17957 23.9654 9.01596 24.391 9.01966C24.8165 9.02336 25.2236 9.19406 25.5246 9.49501C25.8255 9.79595 25.9962 10.2031 25.9999 10.6286C26.0036 11.0542 25.84 11.4642 25.5444 11.7704L19.8049 17.5098L25.5444 23.2492C25.84 23.5554 26.0036 23.9654 25.9999 24.391C25.9962 24.8165 25.8255 25.2236 25.5246 25.5246C25.2236 25.8255 24.8165 25.9962 24.391 25.9999C23.9654 26.0036 23.5554 25.84 23.2492 25.5444L17.5098 19.8049L11.7704 25.5444C11.4642 25.84 11.0542 26.0036 10.6286 25.9999C10.2031 25.9962 9.79595 25.8255 9.49501 25.5246C9.19406 25.2236 9.02336 24.8165 9.01966 24.391C9.01596 23.9654 9.17957 23.5554 9.47523 23.2492L15.2147 17.5098L9.47523 11.7704C9.17094 11.466 9 11.0532 9 10.6228C9 10.1924 9.17094 9.77962 9.47523 9.47523Z" fill="white"/>
    </svg>
  </div>
</div>

<style>
  :global(*) {
    box-sizing: border-box;
  }

  .property-1default, .property-1hovered {
    position: absolute;
    left: 20px;
    top: 20px;
  }

  .property-1hovered {
    display: none;
  }

  .buttonclose {
    width: 75px;
    height: 75px;
    position: relative;
    overflow: hidden;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    border-radius: 5px;
  }

  .buttonclose:hover .property-1default {
    display: none;
  }
  
  .buttonclose:hover .property-1hovered {
    display: block;
  }
</style>