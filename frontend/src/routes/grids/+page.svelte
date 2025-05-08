<script lang="ts">
  import BackgroundLayout from "$lib/components/BackgroundLayout.svelte";
  import Navbar from "$lib/components/Navbar.svelte";
  import GridPreviewButton from "$lib/components/GridPreviewButton.svelte";
  import GridAddButton from "$lib/components/GridAddButton.svelte";
  import BackButton from "$lib/components/BackButton.svelte";
  import LeaderboardButton from "$lib/components/LeaderboardButton.svelte";
  import GridPopup from "$lib/components/GridPopup.svelte";
  import { goto } from "$app/navigation";
  import { onMount } from "svelte";
  import { gridService, type Grid } from "$lib/services/api";
  
  let grids: Grid[] = [];
  let loading = true;
  let error = false;
  let selectedGridId: number | null = null;
  let showGridPopup = false;
  
  onMount(async () => {
    try {
      loading = true;
      grids = await gridService.getAllGrids();
    } catch (err) {
      console.error("Error fetching grids:", err);
      error = true;
    } finally {
      loading = false;
    }
  });

  function handleLeaderboardClick() {
    console.log('Navigate to leaderboard');
  }

  function handleBackClick() {
    goto('/');
  }

  function handleAvatarClick() {
    console.log('Open avatar/profile menu');
  }

  function handleAddGridClick() {
    goto('/grids/add');
  }

  function handleGridClick(gridId: number) {
    console.log(`Grid ${gridId} clicked`);
    selectedGridId = gridId;
    showGridPopup = true;
  }
  
  function handlePopupClose() {
    showGridPopup = false;
  }

  const customNavButtons = [
    {
      component: LeaderboardButton,
      props: { onClick: handleLeaderboardClick }
    },
    {
      component: BackButton,
      props: { onClick: handleBackClick }
    }
  ];
</script>

<BackgroundLayout>
  <div class="grids-page">
    <Navbar
      showLeaderboardButton={false}
      showGridButton={false}
      customButtons={customNavButtons}
      avatarUrl="https://placehold.co/88x88"
      avatarAlt="User avatar"
    />
    
    <div class="grids-container">
      <div on:click={handleAddGridClick} on:keydown={(e) => e.key === 'Enter' && handleAddGridClick()} role="button" tabindex="0">
        <GridAddButton />
      </div>
      
      {#if loading}
        <div class="loading">Loading grids...</div>
      {:else if error}
        <div class="error">
          <p>Failed to load grids from the server.</p>
          <button on:click={() => window.location.reload()}>Try again</button>
        </div>
      {:else if grids.length === 0}
        <div class="no-grids">No grids found. Create the first one!</div>
      {:else}
        {#each grids as grid (grid.gridId)}
          <div on:click={() => handleGridClick(grid.gridId)} on:keydown={(e) => e.key === 'Enter' && handleGridClick(grid.gridId)} role="button" tabindex="0">
            <GridPreviewButton 
              gridName={grid.name}
              author={grid.creator || 'Unknown'}
              isOfficial={grid.isOfficial}
              characters={grid.characters}
            />
          </div>
        {/each}
      {/if}
    </div>
  </div>

  {#if selectedGridId !== null}
    <GridPopup 
      gridId={selectedGridId} 
      visible={showGridPopup} 
      on:close={handlePopupClose} 
    />
  {/if}
</BackgroundLayout>

<style>
  .grids-page {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .grids-container {
    max-width: 1620px;
    padding: 120px 20px 20px 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 70px;
    justify-content: flex-start;
    align-items: flex-start;
  }

  .grids-container > div {
    cursor: pointer;
  }
  
  .loading, .error, .no-grids {
    width: 100%;
    padding: 2rem;
    text-align: center;
    font-size: 1.5rem;
    color: white;
  }
  
  .error {
    color: #ff3e00;
  }
  
  .error button {
    background: #ff3e00;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    margin-top: 1rem;
    border-radius: 4px;
    cursor: pointer;
  }
</style>