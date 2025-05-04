<script lang="ts">
  import LeaderboardButton from './LeaderboardButton.svelte';
  import GridButton from './GridButton.svelte';
  import AvatarButton from './AvatarButton.svelte';
  
  export let avatarUrl: string = "https://placehold.co/88x88";
  export let avatarAlt: string = "User avatar";
  export let onLeaderboardClick: () => void = () => {};
  export let onGridClick: () => void = () => {};
  export let onAvatarClick: () => void = () => {};
  
  export let showLeaderboardButton: boolean = true;
  export let showGridButton: boolean = true;
  
  export let customButtons: any[] = [];
</script>

<div class="navbar">
  <div class="button-container">
    {#if customButtons.length > 0}
      {#each customButtons as button}
        <svelte:component this={button.component} {...button.props} />
      {/each}
    {:else}
      {#if showLeaderboardButton}
        <LeaderboardButton onClick={onLeaderboardClick} />
      {/if}
      {#if showGridButton}
        <GridButton onClick={onGridClick} />
      {/if}
    {/if}
  </div>
  
  <AvatarButton imageUrl={avatarUrl} altText={avatarAlt} onClick={onAvatarClick} />
</div>

<style>
  .navbar {
    display: inline-flex;
    justify-content: flex-end;
    align-items: center;
    gap: 30px;
    padding: 20px;
    width: 100%;
    position: fixed;
    top: 0;
    right: 0;
    z-index: 100;
  }
  
  .button-container {
    display: flex;
    gap: 30px;
    justify-content: flex-end;
    align-items: center;
  }
</style>