<script lang="ts">
  import AvatarPreview from './AvatarPreview.svelte';
  
  export let avatarSrc: string = "https://placehold.co/88x88";
  export let username: string = "User";
  export let message: string = "Message content";
  export let isCurrentUser: boolean = false;
</script>

<div class={isCurrentUser ? "chat-message right-chat" : "chat-message left-chat"}>
  {#if !isCurrentUser}
    <div data-state="default" class="avatar">
      <AvatarPreview imageUrl={avatarSrc} altText={`${username}'s avatar`} borderColor="#2E2E2E" />
    </div>
  {/if}
  
  <div class="message-content-wrapper">
    <div class="username-container">
      <span class="username-text">{username}</span>
    </div>
    
    <div data-svg-wrapper class="polygon">
      {#if isCurrentUser}
        <svg width="16" height="11" viewBox="0 0 16 11" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0.92334 8.46762C0.92334 10.0222 2.61928 10.9824 3.95233 10.1826L14.7317 3.71499C16.4711 2.67136 15.7312 0 13.7027 0H2.92334C1.81877 0 0.92334 0.895431 0.92334 2V8.46762Z" fill="white"/>
        </svg>
      {:else}
        <svg width="15" height="11" viewBox="0 0 15 11" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M15 8.46762C15 10.0222 13.3041 10.9824 11.971 10.1826L1.19165 3.71499C-0.547731 2.67136 0.192189 0 2.22063 0H13C14.1046 0 15 0.895431 15 2V8.46762Z" fill="white"/>
        </svg>
      {/if}
    </div>
    
    <div class="expanding-chat">
      <div class="message-content">
        <span class="message-text">{message}</span>
      </div>
    </div>
  </div>
  
  {#if isCurrentUser}
    <div data-state="default" class="avatar">
      <AvatarPreview imageUrl={avatarSrc} altText={`${username}'s avatar`} borderColor="#2E2E2E" />
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
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    word-wrap: break-word;
  }

  .username-container {
    width: 213px;
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
  
  .left-chat .username-container span {
    text-align: left;
    padding-left: 0;
    margin-left: 0;
  }
  
  .right-chat .username-container span {
    text-align: right;
    padding-right: 0;
    margin-right: 0;
  }

  .message-text {
    color: #444444;
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
    padding-left: 14px;
    padding-right: 14px;
    padding-top: 11px;
    padding-bottom: 11px;
    position: relative;
    background: white;
    justify-content: flex-start;
    align-items: flex-start;
    gap: 10px;
    display: inline-flex;
    width: max-content;
    max-width: 500px;
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

  .message-content-wrapper {
    display: flex;
    flex-direction: column;
    position: relative;
    flex: 1;
  }
  
  .left-chat .message-content-wrapper {
    margin-left: 12px;
  }
  
  .right-chat .message-content-wrapper {
    margin-right: 12px;
    align-items: flex-end;
  }

  .polygon {
    position: absolute;
    top: 27px;
    z-index: 2;
    line-height: 0;
    font-size: 0;
  }
  
  .left-chat .polygon {
    left: 0;
  }
  
  .right-chat .polygon {
    right: 0;
  }

  .polygon svg {
    display: block;
  }
</style>