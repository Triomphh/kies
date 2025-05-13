<script lang="ts">
  import CharacterCard from './CharacterCard.svelte';
  import type { Character as GameCharacter } from '$lib/services/gameService';
  import { createEventDispatcher } from 'svelte';

  export let characters: GameCharacter[] = [];
  export let isGuessingMode: boolean = false;
  $: console.log('[PlayerGameBoard.svelte] isGuessingMode prop updated to:', isGuessingMode);

  const dispatch = createEventDispatcher();


  function handleCardClick(characterId: string, characterName: string, isFlipped: boolean) {
    console.log('[PlayerGameBoard.svelte] handleCardClick triggered. CharacterId:', characterId, 'isGuessingMode prop at click time:', isGuessingMode);
    if (isGuessingMode) {
      console.log('[PlayerGameBoard.svelte] Dispatching "guessCharacter" event. CharacterId:', characterId);
      dispatch('guessCharacter', { characterId, characterName });
    } else {
      const charIndex = characters.findIndex(c => c.id === characterId);
      if (charIndex !== -1) {
        console.log(`[PlayerGameBoard.svelte] Optimistically flipping character ${characterId} locally.`);
        characters[charIndex].isFlipped = !characters[charIndex].isFlipped;
        characters = characters;
      } else {
         console.warn(`[PlayerGameBoard.svelte] Could not find character ${characterId} for optimistic flip.`);
      }

      console.log('[PlayerGameBoard.svelte] Dispatching "flipCard" event. CharacterId:', characterId);
      dispatch('flipCard', { characterId, characterName, isFlipped });
    }
  }

</script>

<div class="player-game-board">
  {#each characters as character (character.id)}
    <CharacterCard
      characterName={character.characterName}
      imageUrl={character.imageUrl}
      isFlipped={character.isFlipped}
      selectable={character.selectable}
      secret={character.secret}
      guessingActive={isGuessingMode}
      on:click={() => handleCardClick(character.id, character.characterName, character.isFlipped)}
    />
  {/each}
</div>

<style>
  .player-game-board {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-template-rows: repeat(4, 1fr);
    gap: 10px;
    padding: 10px;
    border-radius: 8px;
    max-width: calc(8 * (121px + 10px) + 10px);
    margin: auto;
  }
</style>