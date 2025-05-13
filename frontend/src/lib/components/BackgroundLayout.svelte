<script>
  import Fireworks from './Fireworks.svelte';
  export let showClouds = true;
  export let showGrass = true;
  export let showFireworks = false; // Default to false
</script>

<div class="background-container">
  {#if showGrass}
    <div class="grass"></div>
  {/if}
  
  {#if showClouds}
    <div class="cloud1"></div>
    <div class="cloud2"></div>
  {/if}

  {#if showFireworks}
    <Fireworks />
  {/if}
  
  <div class="content">
    <slot />
  </div>
</div>

<style>
  /* Enlève la margin de base du <body> qui cause une bordure blanche */
  :global(body), :global(html) {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  .background-container {
    width: 100%;
    height: 100%;
    min-height: 100vh;
    position: relative;
    /* background: #BDDFFF; */
    background: linear-gradient(to bottom, #87CEEB, #1E90FF);
    overflow: hidden;
  }

  .grass {
    width: 110%;
    height: 232px;
    left: -5%;
    bottom: 0;
    position: fixed;
    background: #56BF72;
    box-shadow: 242.39999389648438px 242.39999389648438px 242.39999389648438px;
    filter: blur(121.20px);
    z-index: 1;
    transform: translateY(50%);
  }

  .cloud1 {
    width: 352px;
    height: 88px;
    flex-shrink: 0;
    left: 183px;
    top: 128px;
    position: fixed;
    background: rgba(255, 255, 255, 0.60);
    border-radius: 352px;
    filter: blur(25.600000381469727px);
    z-index: 2;
  }

  .cloud2 {
    width: 352px;
    height: 100px;
    flex-shrink: 0;
    left: calc(100% - 544px);
    top: 149px;
    position: fixed;
    background: rgba(255, 255, 255, 0.60);
    border-radius: 352px;
    filter: blur(27.899999618530273px);
    z-index: 2;
  }

  .content {
    position: relative;
    z-index: 10;
    height: 100%;
    width: 100%;
  }
</style>