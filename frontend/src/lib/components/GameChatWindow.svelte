<script lang="ts">
  import ChatMessage from './ChatMessage.svelte';
  import ChatInput from './ChatInput.svelte';
  import { afterUpdate } from 'svelte';
  
  export let messages: Array<{
    username: string;
    avatarSrc: string;
    message: string;
    isCurrentUser: boolean;
  }> = [
    {
      username: "JACOB",
      avatarSrc: "https://placehold.co/88x88",
      message: "salut mon caillou",
      isCurrentUser: true
    },
    {
      username: "JOJO",
      avatarSrc: "https://placehold.co/88x88",
      message: "pk je suis sur les images??",
      isCurrentUser: false
    }
  ];
  
  export let currentUsername: string = "";
  
  let inputMessage: string = "";
  let messagesContainer: HTMLElement;
  let shouldAutoScroll = true;
  let currentUserAvatar: string = "/images/cop.png";
  
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
    if (inputMessage.trim()) {
      shouldAutoScroll = true;
      
      messages = [
        ...messages,
        {
          username: currentUsername || "YOU",
          avatarSrc: currentUserAvatar,
          message: inputMessage,
          isCurrentUser: true
        }
      ];
      
      inputMessage = "";
    }
  }
</script>

<div class="game-chat-window">
  <div class="messages-container" 
       bind:this={messagesContainer} 
       on:scroll={handleScroll}>
    {#each messages as msg, i}
      <div class="message-wrapper">
        <ChatMessage
          avatarSrc={msg.avatarSrc}
          username={msg.username}
          message={msg.message}
          isCurrentUser={msg.isCurrentUser}
        />
      </div>
    {/each}
  </div>
  
  <div class="input-container">
    <ChatInput
      bind:value={inputMessage}
      onInput={handleInput}
      onSubmit={handleSubmit}
    />
  </div>
</div>

<style>
  .game-chat-window {
    width: 700px;
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
</style>