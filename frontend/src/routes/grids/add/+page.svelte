<script lang="ts">
  import BackgroundLayout from "$lib/components/BackgroundLayout.svelte";
  import Navbar from "$lib/components/Navbar.svelte";
  import AddCard from "$lib/components/AddCard.svelte";
  import BackButton from "$lib/components/BackButton.svelte";
  import LeaderboardButton from "$lib/components/LeaderboardButton.svelte";
  import LeaveConfirmationDialog from "$lib/components/LeaveConfirmationDialog.svelte";
  import { goto } from "$app/navigation";

  let isGridPanelOpen: boolean = true;
  let isLeaveDialogVisible: boolean = false;
  let navigateTo: string = '';
  
  let characters: Array<{name: string, imageUrl?: string}> = Array(32).fill(null).map(() => ({ name: "" }));

  function handleLeaderboardClick() {
    console.log('Navigate to leaderboard');
  }

  function handleBackClick() {
    isLeaveDialogVisible = true;
    navigateTo = '/';
  }

  function confirmLeave() {
    isLeaveDialogVisible = false;
    goto(navigateTo);
  }

  function cancelLeave() {
    isLeaveDialogVisible = false;
    navigateTo = '';
  }

  function handleAvatarClick() {
    console.log('Open avatar/profile menu');
  }

  function handleAddCharacter(index: number, name: string = '') {
    console.log(`Adding/editing character at index ${index} with name: ${name}`);
    characters[index].name = name;
    characters = [...characters];
  }

  function toggleGridPanel() {
    isLeaveDialogVisible = true;
    navigateTo = '/grids';
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
  <div class="game-container">
    <div class="navbar-wrapper">
      <Navbar
        showLeaderboardButton={false}
        showGridButton={false}
        customButtons={customNavButtons}
        avatarUrl="https://placehold.co/88x88"
        avatarAlt="User avatar"
        onAvatarClick={handleAvatarClick}
      />
    </div>
    
    <div class="game-content">
      <div class="game-grid-container">
        <div class="game-grid">
          {#each characters as character, index}
            <div class="character-card">
              <AddCard 
                onClick={() => handleAddCharacter(index)}
              />
            </div>
          {/each}
        </div>
      </div>
      
      <div class="grid-info-panel" class:open={isGridPanelOpen}>
        <div class="close-button" on:click={toggleGridPanel} on:keydown={(e) => e.key === 'Enter' && toggleGridPanel()} role="button" tabindex="0" aria-label="Toggle grid info panel">
          <svg width="35" height="35" viewBox="0 0 35 35" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="1.5" y="1.5" width="32" height="32" rx="3.5" fill="#DA6767" stroke="#A75A5A" stroke-width="3"/>
            <path d="M9.47523 9.47523C9.77962 9.17094 10.1924 9 10.6228 9C11.0532 9 11.466 9.17094 11.7704 9.47523L17.5098 15.2147L23.2492 9.47523C23.5554 9.17957 23.9654 9.01596 24.391 9.01966C24.8165 9.02336 25.2236 9.19406 25.5246 9.49501C25.8255 9.79595 25.9962 10.2031 25.9999 10.6286C26.0036 11.0542 25.84 11.4642 25.5444 11.7704L19.8049 17.5098L25.5444 23.2492C25.84 23.5554 26.0036 23.9654 25.9999 24.391C25.9962 24.8165 25.8255 25.2236 25.5246 25.5246C25.2236 25.8255 24.8165 25.9962 24.391 25.9999C23.9654 26.0036 23.5554 25.84 23.2492 25.5444L17.5098 19.8049L11.7704 25.5444C11.4642 25.84 11.0542 26.0036 10.6286 25.9999C10.2031 25.9962 9.79595 25.8255 9.49501 25.5246C9.19406 25.2236 9.02336 24.8165 9.01966 24.391C9.01596 23.9654 9.17957 23.5554 9.47523 23.2492L15.2147 17.5098L9.47523 11.7704C9.17094 11.466 9 11.0532 9 10.6228C9 10.1924 9.17094 9.77962 9.47523 9.47523Z" fill="white"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
  
  <LeaveConfirmationDialog 
    isVisible={isLeaveDialogVisible}
    onClose={cancelLeave}
    onLeave={confirmLeave}
  />
</BackgroundLayout>

<style>
  :global(body), :global(html) {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Comic Neue', sans-serif;
  }

  .game-container {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
  }
  
  .navbar-wrapper {
    width: 100%;
    height: 85px;
    margin-bottom: 20px;
  }
  
  .game-content {
    display: flex;
    gap: 21px;
    justify-content: center;
    align-items: flex-start;
    width: 100%;
    max-width: 1620px;
    margin: 20px auto 0;
  }
  
  .game-grid-container {
    flex: 0 1 1073px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .game-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-template-rows: repeat(4, auto);
    gap: 15px;
    justify-content: center;
    width: 100%;
    max-width: 1073px;
    height: 710px;
    padding: 4px;
  }
  
  .character-card {
    cursor: pointer;
  }

  .grid-info-panel {
    flex: 0 0 526px;
    width: 526px;
    height: 710px;
    background: white;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    position: relative;
    padding: 30px;
    overflow: hidden;
    backdrop-filter: blur(5px);
  }

  .close-button {
    position: absolute;
    top: 17px;
    right: 17px;
    cursor: pointer;
    z-index: 10;
  }
  
  @media (max-width: 1600px) {
    .game-content {
      flex-direction: column;
      align-items: center;
    }
    
    .game-grid-container {
      flex: 0 1 auto;
      width: 100%;
      max-width: 550px;
    }
    
    .game-grid {
      grid-template-columns: repeat(4, 1fr);
      grid-template-rows: repeat(8, auto);
      max-width: 550px;
    }
    
    .grid-info-panel {
      margin-top: 20px;
      flex: 0 0 600px;
      width: 600px;
    }
  }
</style>