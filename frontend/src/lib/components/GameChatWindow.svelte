<script lang="ts">
  import type { DisplayMessage } from '$lib/types';
  import ChatMessage from './ChatMessage.svelte';
  import QuestionChatMessage from './QuestionChatMessage.svelte';
  import ChatInput from './ChatInput.svelte';
  import { afterUpdate, createEventDispatcher } from 'svelte';
  import { authStore } from '$lib/services/authService';
  import { get } from 'svelte/store';
  
  export let messages: DisplayMessage[] = [];
  export let isInteractionAllowed: boolean = false;
  
  const dispatch = createEventDispatcher();

  let inputMessage: string = "";
  let messagesContainer: HTMLElement;
  let shouldAutoScroll = true;
  
  function scrollToBottom() {
    if (shouldAutoScroll && messagesContainer) {
      messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
  }
  
  function handleScroll() {
    if (messagesContainer) {
      const distanceFromBottom = 
        messagesContainer.scrollHeight - 
        messagesContainer.scrollTop - 
        messagesContainer.clientHeight;
      
      shouldAutoScroll = distanceFromBottom < 100;
    }
  }
  
  afterUpdate(scrollToBottom);
  
  function handleInput(event: Event) {
  }
  
  function handleSubmit() {
    const trimmedMessage = inputMessage.trim();
    if (!trimmedMessage) {
      return;
    }

    shouldAutoScroll = true;

    if (trimmedMessage.startsWith('/question ')) {
      const questionText = trimmedMessage.substring('/question '.length).trim();
      if (questionText) {
        dispatch('submitQuestion', { question: questionText });
      } else {
        dispatch('submitChatMessage', { text: "Error: /question command cannot be empty."});
      }
    } else {
      dispatch('submitChatMessage', { text: trimmedMessage });
    }
    
    inputMessage = "";
  }

  function handleQuestionAnswered(event: CustomEvent<{ questionId: string; answer: boolean }>) {
    dispatch('submitAnswer', {
      questionId: event.detail.questionId,
      answer: event.detail.answer
    });
  }

  function handleRequestGuessEvent(event: CustomEvent<{ questionId: string }>) {
    dispatch('submitGuessRequest', { questionId: event.detail.questionId });
  }

  function handlePassTurnEvent(event: CustomEvent<{ questionId: string }>) {
    dispatch('submitPassTurn', { questionId: event.detail.questionId });
  }
</script>

<div class="game-chat-window">
  <div class="messages-container"
       bind:this={messagesContainer}
       on:scroll={handleScroll}>
    {#each messages as msg (msg.id)}
      <div class="message-wrapper">
        {#if msg.type === 'event' && msg.eventClass === 'question' && msg.questionDetails}
          {@const currentUserDetails = get(authStore)}
          {@const qd = msg.questionDetails}
          <QuestionChatMessage
            timestamp={msg.timestamp}
            avatarSrc={msg.avatarSrc || "https://placehold.co/88x88"}
            username={msg.username || "System"}
            questionText={qd.originalQuestionText || msg.text}
            questionId={qd.questionId}
            isCurrentUser={currentUserDetails.id?.toString() === qd.askingPlayerId}
            isTargetUser={currentUserDetails.id?.toString() === qd.targetPlayerId && !qd.isAnswered}
            isAnswered={qd.isAnswered}
            on:answer={handleQuestionAnswered}
            on:requestGuess={handleRequestGuessEvent}
            on:passTurn={handlePassTurnEvent}
          />
        {:else if msg.type === 'chat'}
          <ChatMessage
            avatarSrc={msg.avatarSrc}
            username={msg.username}
            message={msg.text}
            isCurrentUser={msg.isCurrentUser}
          />
        {:else if msg.type === 'event'}
          <div class="event-message {msg.eventClass || 'system'}">
            <span class="timestamp">[{new Date(msg.timestamp).toLocaleTimeString()}]</span> {msg.text}
          </div>
        {/if}
      </div>
    {/each}
  </div>
  
  <div class="input-container">
    <ChatInput
      bind:value={inputMessage}
      onInput={handleInput}
      onSubmit={handleSubmit}
      disabled={!isInteractionAllowed}
    />
  </div>
</div>

<style>
  .game-chat-window {
    width: 100%;
    height: 710px;
    position: relative;
    background: rgba(0, 0, 0, 0.16);
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    border-radius: 17px;
    backdrop-filter: blur(5px);
    padding: 20px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  
  .messages-container {
    flex: 1;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.3) transparent;
    padding: 10px;
    padding-top: 20px;
    display: flex;
    flex-direction: column;
    margin-bottom: 80px;
    max-height: calc(100% - 100px);
    height: 100%;
  }
  
  .message-wrapper {
    width: 100%;
    position: relative;
    display: flex;
    flex-direction: column;
  }
  
  .messages-container::-webkit-scrollbar {
    width: 8px;
  }
  
  .messages-container::-webkit-scrollbar-track {
    background: transparent;
  }
  
  .messages-container::-webkit-scrollbar-thumb {
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 4px;
  }
  
  .input-container {
    position: absolute;
    bottom: 20px;
    left: 21px;
    width: calc(100% - 42px);
    box-sizing: border-box;
  }

  .event-message {
    padding: 8px 12px;
    margin: 4px 0;
    border-radius: 6px;
    font-size: 0.9em;
    color: #333;
    width: 100%;
    box-sizing: border-box;
  }

  .event-message .timestamp {
    color: #777;
    margin-right: 5px;
    font-size: 0.9em;
  }

  .event-message.system {
    background-color: #e9ecef;
    color: #495057;
    font-style: italic;
  }

  .event-message.question {
    background-color: #e7f3fe;
    color: #0c5460;
  }

  .event-message.answer {
    background-color: #d4edda;
    color: #155724;
  }
  
  .event-message.guess {
    background-color: #fff3cd;
    color: #856404;
  }

  .event-message.error {
    background-color: #f8d7da;
    color: #721c24;
    font-weight: bold;
  }
</style>