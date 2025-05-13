<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  export let availableCharacters: Array<{ id: string; name: string; imageUrl?: string }> = [
    { id: 'char1', name: 'Character 1' },
    { id: 'char2', name: 'Character 2' },
    { id: 'char3', name: 'Character 3' },
  ];

  export let disabled: boolean = false;

  let selectedCharacterId: string | null = null;
  const dispatch = createEventDispatcher();

  function handleGuessSubmit() {
    if (!selectedCharacterId) {
      alert('Please select a character to guess.');
      return;
    }
    dispatch('submitGuess', { characterId: selectedCharacterId });
    selectedCharacterId = null;
  }
</script>

<div class="guess-interface" class:disabled>
  <h4>Make a Guess</h4>
  {#if availableCharacters.length > 0}
    <select bind:value={selectedCharacterId} aria-label="Select character to guess" {disabled}>
      <option value={null} disabled selected>-- Select a Character --</option>
      {#each availableCharacters as character (character.id)}
        <option value={character.id}>{character.name}</option>
      {/each}
    </select>
    <button on:click={handleGuessSubmit} {disabled}>Submit Guess</button>
  {:else}
    <p>No characters available to guess (this shouldn't happen in a normal game).</p>
  {/if}
</div>

<style>
  .guess-interface {
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #f9f9f9;
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
  }

  .guess-interface.disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  h4 {
    margin-top: 0;
    color: #333;
  }

  select {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ccc;
    min-width: 200px;
  }

  button {
    padding: 10px 20px;
    background-color: #ffc107;
    color: black;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.2s;
  }

  button:hover:not(:disabled) {
    background-color: #e0a800;
  }

  button:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
  }
</style>