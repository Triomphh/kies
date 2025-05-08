<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import SmallCharacterCard from './SmallCharacterCard.svelte';
  import CloseButton from './CloseButton.svelte';
  import { gridService, type Grid } from '$lib/services/api';
  import { onMount } from 'svelte';

  export let gridId: number;
  export let visible = false;

  let grid: Grid | null = null;
  let loading = true;
  let error = false;
  const dispatch = createEventDispatcher();
  
  const GRID_SIZE = 32;
  let gridCells = Array(GRID_SIZE).fill(null);

  onMount(async () => {
    if (visible && gridId) {
      await loadGridData();
    }
  });

  $: if (visible && gridId) {
    loadGridData();
  }

  async function loadGridData() {
    try {
      loading = true;
      error = false;
      grid = await gridService.getGridById(gridId);
      
      gridCells = Array(GRID_SIZE).fill(null);
      
      if (grid && grid.characters) {
        grid.characters.forEach((character, index) => {
          if (index < GRID_SIZE) {
            gridCells[index] = character;
          }
        });
      }
    } catch (err) {
      console.error("Error fetching grid:", err);
      error = true;
    } finally {
      loading = false;
    }
  }

  function closePopup() {
    dispatch('close');
  }

  function handleOutsideClick(event: MouseEvent) {
    if (event.target === event.currentTarget) {
      closePopup();
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.key === 'Escape') {
      closePopup();
    }
  }
</script>

<svelte:window on:keydown={handleKeydown} />

{#if visible}
  <div class="popup-backdrop" on:click={handleOutsideClick} on:keydown={()=>{}} role="none">
    <div class="popup-container">
      <div class="close-button-container">
        <CloseButton onClick={closePopup} />
      </div>
      
      {#if loading}
        <div class="loading">Chargement des données...</div>
      {:else if error}
        <div class="error">
          <p>Échec du chargement des données depuis le serveur.</p>
          <button on:click={loadGridData}>Réessayer</button>
        </div>
      {:else if grid}
        <div class="grid-header">
          <h2>{grid.name}</h2>
          <p class="creator">
            {#if grid.isOfficial}
              <span class="official-tag">OFFICIELLE</span>
            {:else}
              <span>par {grid.creator || 'Inconnu'}</span>
            {/if}
          </p>
        </div>
        
        <div class="grid-layout">
          {#each gridCells as cell, index}
            <div class="grid-cell">
              {#if cell}
                <SmallCharacterCard 
                  characterName={cell.name} 
                  imageUrl={cell.imageUrl} 
                />
              {:else}
                <div class="empty-cell"></div>
              {/if}
            </div>
          {/each}
        </div>
      {:else}
        <div class="error">Grille introuvable</div>
      {/if}
    </div>
  </div>
{/if}

<style>
  .popup-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    overflow-y: auto;
    backdrop-filter: blur(5px);
  }

  .popup-container {
    background-color: #222;
    border-radius: 15px;
    width: 90%;
    max-width: 1000px;
    max-height: 90vh;
    overflow-y: auto;
    padding: 30px;
    position: relative;
    color: white;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.5);
    border: 2px solid #444;
  }

  .close-button-container {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 10;
  }

  .grid-header {
    text-align: center;
    margin-bottom: 20px;
  }

  .grid-header h2 {
    font-family: 'Roboto Mono', monospace;
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 5px;
  }

  .creator {
    font-family: 'Roboto Mono', monospace;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.77);
  }

  .official-tag {
    font-family: "Roboto Mono";
    font-size: 14px;
    font-weight: 700;
    background: linear-gradient(86deg, rgba(144, 0, 255, 0.77) -2.12%, rgba(255, 204, 0, 0.77) 49.04%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .grid-layout {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 15px;
    justify-items: center;
  }

  .grid-cell {
    width: 85px;
    height: 116px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .empty-cell {
    width: 85px;
    height: 116px;
    border-radius: 8px;
    border: 2px dashed rgba(255, 255, 255, 0.3);
    background-color: rgba(255, 255, 255, 0.05);
  }

  .loading, .error {
    text-align: center;
    padding: 40px;
    font-family: 'Roboto Mono', monospace;
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

  @media (max-width: 768px) {
    .grid-layout {
      grid-template-columns: repeat(4, 1fr);
    }
  }
</style>
