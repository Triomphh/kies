<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let disabled: boolean = false;
  export let questionToAnswer: string | null = null;

  function sendAnswer(answer: boolean) {
    dispatch('submitAnswer', { answer });
  }
</script>

<div class="answer-interface" class:disabled>
  {#if questionToAnswer}
    <p class="question-prompt">Answering: "<em>{questionToAnswer}</em>"</p>
  {/if}
  <button
    class="answer-button yes"
    on:click={() => sendAnswer(true)}
    {disabled}
    aria-label="Answer Yes"
  >
    YES
  </button>
  <button
    class="answer-button no"
    on:click={() => sendAnswer(false)}
    {disabled}
    aria-label="Answer No"
  >
    NO
  </button>
</div>

<style>
  .answer-interface {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #f9f9f9;
  }

  .answer-interface.disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  .question-prompt {
    font-style: italic;
    color: #555;
    margin-bottom: 10px;
    text-align: center;
  }

  .answer-button {
    padding: 10px 20px;
    font-size: 16px;
    font-weight: bold;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.2s, transform 0.1s;
    min-width: 100px;
  }

  .answer-button.yes {
    background-color: #28a745;
  }

  .answer-button.yes:hover:not(:disabled) {
    background-color: #218838;
    transform: translateY(-1px);
  }

  .answer-button.no {
    background-color: #dc3545;
  }

  .answer-button.no:hover:not(:disabled) {
    background-color: #c82333;
    transform: translateY(-1px);
  }

  .answer-button:disabled {
    cursor: not-allowed;
    background-color: #6c757d;
  }
</style>