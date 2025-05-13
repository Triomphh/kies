<script lang="ts">
  import AvatarPreview from './AvatarPreview.svelte';
  import { createEventDispatcher } from 'svelte';

  export let avatarSrc: string = "https://placehold.co/88x88";
  export let username: string = "User";
  export let questionText: string = "Is this a question?";
  export let questionId: string;
  export let isCurrentUser: boolean = false;
  export let isTargetUser: boolean = false;
  export let isAnswered: boolean = false;
  export let timestamp: Date = new Date();

  let decisionMade = false;

  const dispatch = createEventDispatcher();

  function handleAnswer(answer: boolean) {
    if (!isAnswered && isTargetUser) {
      dispatch('answer', { questionId, answer });
    }
  }

  function handleRequestGuess() {
    if (isCurrentUser && isAnswered && !decisionMade) {
      decisionMade = true;
      dispatch('requestGuess', { questionId });
    }
  }

  function handlePassTurn() {
    if (isCurrentUser && isAnswered && !decisionMade) {
      decisionMade = true;
      dispatch('passTurn', { questionId });
    }
  }
</script>

<div class="chat-message {isCurrentUser ? 'right-chat' : 'left-chat'}">
  {#if !isCurrentUser}
    <div class="avatar">
      <AvatarPreview imageUrl={avatarSrc} altText={`{username}'s avatar`} borderColor="#2E2E2E" />
    </div>
  {/if}
  
  <div class="message-content-wrapper">
    <div class="username-container">
      <span class="username-text">{username}</span>
    </div>
    
    <div class="polygon-wrapper">
      <div class="polygon">
        {#if isCurrentUser}
          <svg width="16" height="11" viewBox="0 0 16 11" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0.92334 8.46762C0.92334 10.0222 2.61928 10.9824 3.95233 10.1826L14.7317 3.71499C16.4711 2.67136 15.7312 0 13.7027 0H2.92334C1.81877 0 0.92334 0.895431 0.92334 2V8.46762Z" fill="#E0E0E0"/>
          </svg>
        {:else}
          <svg width="15" height="11" viewBox="0 0 15 11" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 8.46762C15 10.0222 13.3041 10.9824 11.971 10.1826L1.19165 3.71499C-0.547731 2.67136 0.192189 0 2.22063 0H13C14.1046 0 15 0.895431 15 2V8.46762Z" fill="#E0E0E0"/>
          </svg>
        {/if}
      </div>
    </div>
    
    <div class="expanding-chat question-bubble">
      <div class="message-content">
        <span class="message-text">{questionText}</span>
      </div>
    </div>

    {#if isTargetUser && !isAnswered}
      <div class="answer-buttons-container">
        <button class="answer-button oui-button" on:click={() => handleAnswer(true)}>
          OUI
        </button>
        <button class="answer-button non-button" on:click={() => handleAnswer(false)}>
          NON
        </button>
      </div>
    {:else if isCurrentUser && isAnswered}
      {#if !decisionMade}
        <div class="answer-buttons-container">
          <button class="answer-button oui-button" on:click={handleRequestGuess}>
            Deviner
          </button>
          <button class="answer-button non-button" on:click={handlePassTurn}>
            Ne pas deviner
          </button>
        </div>
      {/if}
    {:else if !isCurrentUser && isAnswered}
      <div class="answered-indicator">
        (Répondu par l'adversaire)
      </div>
    {/if}
  </div>
  
  {#if isCurrentUser}
    <div class="avatar">
      <AvatarPreview imageUrl={avatarSrc} altText={`{username}'s avatar`} borderColor="#2E2E2E" />
    </div>
  {/if}
</div>

<style>
  .chat-message {
    width: 100%;
    min-height: 88px;
    height: auto;
    position: relative;
    display: flex;
    margin-bottom: 20px;
    font-family: 'Comic Neue', cursive;
  }
  
  .left-chat {
    justify-content: flex-start;
  }
  
  .right-chat {
    justify-content: flex-end;
  }

  .avatar {
    width: 88px;
    height: 88px;
    position: relative;
    flex-shrink: 0;
  }

  .username-text {
    color: rgba(255, 255, 255, 0.70);
    font-size: 24px;
    font-family: 'Dongle', sans-serif;
    font-weight: 700;
    line-height: normal;
    word-wrap: break-word;
  }

  .username-container {
    height: 10px;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex-shrink: 0;
    margin-bottom: 5px;
    margin-top: 12px;
  }
  
  .left-chat .username-container {
    text-align: left;
    margin-left: 12px;
  }
  
  .right-chat .username-container {
    text-align: right;
    margin-right: 12px;
    align-self: flex-end;
  }

  .message-text {
    color: #333333;
    font-size: 24px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    word-wrap: break-word;
    text-align: left;
    overflow-wrap: break-word;
    word-break: break-word;
    max-width: 100%;
  }

  .message-content {
    justify-content: center;
    display: flex;
    flex-direction: column;
    text-align: left;
    align-items: flex-start;
    width: 100%;
  }

  .expanding-chat {
    padding: 11px 14px;
    position: relative;
    background: #E0E0E0;
    justify-content: flex-start;
    align-items: flex-start;
    gap: 10px;
    display: inline-flex;
    width: max-content;
    max-width: 500px;
    border-radius: 4px;
  }
  
  .left-chat .expanding-chat {
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    border-bottom-left-radius: 4px;
    margin-left: 12px;
  }
  
  .right-chat .expanding-chat {
    border-top-left-radius: 4px;
    border-bottom-right-radius: 4px;
    border-bottom-left-radius: 4px;
    margin-right: 12px;
    align-self: flex-end;
  }
  
  .question-bubble {
     background: #E0E0E0;
  }

  .message-content-wrapper {
    display: flex;
    flex-direction: column;
    position: relative;
    flex: 1;
    max-width: calc(100% - 100px);
  }
  
  .left-chat .message-content-wrapper {
    margin-left: 12px;
  }
  
  .right-chat .message-content-wrapper {
    margin-right: 12px;
    align-items: flex-end;
  }

  .polygon-wrapper {
    position: absolute;
    top: 27px;
    z-index: 1;
    line-height: 0;
  }
  
  .left-chat .polygon-wrapper {
    left: 0;
  }
  
  .right-chat .polygon-wrapper {
    right: 0;
  }

  .polygon svg {
    display: block;
  }

  .answer-buttons-container {
    display: flex;
    gap: 10px;
    margin-top: 10px;
  }

  .left-chat .answer-buttons-container {
    margin-left: 12px;
    align-self: flex-start;
  }

  .right-chat .answer-buttons-container {
     margin-right: 12px;
     align-self: flex-end;
  }


  .answer-button {
    height: 42px;
    padding: 8px 18px;
    box-shadow: 0px 3px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: inline-flex;
    cursor: pointer;
    user-select: none;
    font-family: 'Comic Neue', sans-serif;
    font-weight: 700;
    font-size: 24px;
    border: none;
    transition: background-color 0.2s ease, box-shadow 0.2s ease, padding 0.2s ease;
  }

  .oui-button {
    background: #53BD4E;
    color: white;
  }
  .oui-button:hover {
    background: #4AA945;
  }
  .oui-button:active {
    background: #4AA945;
    box-shadow: 0px 3px 0px #3E8E3A inset;
    padding-top: 11px;
    padding-bottom: 5px;
  }

  .non-button {
    background: #F07167;
    color: white;
  }
  .non-button:hover {
    background: #D95A50;
  }
  .non-button:active {
    background: #D95A50;
    box-shadow: 0px 3px 0px #C04F46 inset;
    padding-top: 11px;
    padding-bottom: 5px;
  }
  
  .answered-indicator {
    margin-top: 8px;
    font-size: 18px;
    color: #777;
    font-style: italic;
    padding: 4px 8px;
    background-color: #f0f0f0;
    border-radius: 4px;
    display: inline-block;
  }

  .left-chat .answered-indicator {
    margin-left: 12px;
    align-self: flex-start;
  }

  .right-chat .answered-indicator {
    margin-right: 12px;
    align-self: flex-end;
  }

</style>