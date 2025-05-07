<script lang="ts">
  import LeaderboardButton from './LeaderboardButton.svelte';
  import GridButton from './GridButton.svelte';
  import AvatarButton from './AvatarButton.svelte';
  import NicknameInput from './NicknameInput.svelte';
  import { goto } from '$app/navigation';
  import { authService } from '$lib/services/authService';
  import { onMount, onDestroy } from 'svelte';
  import { browser } from '$app/environment';
  
  export let avatarUrl: string = "https://placehold.co/88x88";
  export let avatarAlt: string = "User avatar";
  export let onLeaderboardClick: () => void = () => {};
  export let onGridClick: () => void = () => {};
  
  export let showLeaderboardButton: boolean = true;
  export let showGridButton: boolean = true;
  export let showNicknameInput: boolean = true;
  
  export let customButtons: any[] = [];
  
  let isAuthenticated = false;
  let isDropdownOpen = false;
  let dropdownRef: HTMLDivElement;
  let nickname = '';
  
  onMount(() => {
    if (browser) {
      isAuthenticated = authService.isAuthenticated();
      
      const userInfo = authService.getUserInfo();
      nickname = userInfo.username || localStorage.getItem('playerNickname') || '';
      
      document.addEventListener('click', handleClickOutside);
    }
  });
  
  onDestroy(() => {
    if (browser) {
      document.removeEventListener('click', handleClickOutside);
    }
  });
  
  function handleClickOutside(event: MouseEvent) {
    if (dropdownRef && !dropdownRef.contains(event.target as Node)) {
      isDropdownOpen = false;
    }
  }
  
  function toggleDropdown(event: MouseEvent) {
    event.stopPropagation();
    isDropdownOpen = !isDropdownOpen;
  }
  
  function handleLoginClick() {
    isDropdownOpen = false;
    goto('/login');
  }
  
  function handleRegisterClick() {
    isDropdownOpen = false;
    goto('/register');
  }
  
  function handleLogoutClick() {
    isDropdownOpen = false;
    authService.logout();
    goto('/');
    window.location.reload();
  }
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
  
  {#if showNicknameInput}
    <div class="nickname-container">
      <NicknameInput bind:value={nickname} />
    </div>
  {/if}
  
  <div class="avatar-container" bind:this={dropdownRef}>
    <div class="avatar-wrapper" on:click={toggleDropdown}>
      <AvatarButton imageUrl={avatarUrl} altText={avatarAlt} />
    </div>
    
    {#if isDropdownOpen}
      <div class="dropdown-menu">
        {#if isAuthenticated}
          <div class="dropdown-header">
            <strong>{nickname}</strong>
          </div>
          <button class="dropdown-item" on:click={handleLogoutClick}>
            Logout
          </button>
        {:else}
          <button class="dropdown-item" on:click={handleLoginClick}>
            Login
          </button>
          <button class="dropdown-item" on:click={handleRegisterClick}>
            Register
          </button>
        {/if}
      </div>
    {/if}
  </div>
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
    box-sizing: border-box;
  }
  
  .button-container {
    display: flex;
    gap: 30px;
    justify-content: flex-end;
    align-items: center;
  }
  
  .nickname-container {
    display: flex;
    align-items: center;
  }
  
  .avatar-container {
    position: relative;
    display: inline-block;
  }
  
  .avatar-wrapper {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 8px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    min-width: 150px;
    z-index: 101;
    overflow: hidden;
  }
  
  .dropdown-header {
    padding: 12px 16px;
    background-color: #f5f5f5;
    border-bottom: 1px solid #eee;
    font-size: 14px;
  }
  
  .dropdown-item {
    display: block;
    padding: 12px 16px;
    width: 100%;
    text-align: left;
    background: none;
    border: none;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }
  
  .dropdown-item:hover {
    background-color: #f5f5f5;
  }
  
  .dropdown-item:not(:last-child) {
    border-bottom: 1px solid #eee;
  }
</style>