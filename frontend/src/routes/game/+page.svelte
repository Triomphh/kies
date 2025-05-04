<script lang="ts">
  import BackgroundLayout from "$lib/components/BackgroundLayout.svelte";
  import Navbar from "$lib/components/Navbar.svelte";
  import CharacterCard from "$lib/components/CharacterCard.svelte";
  import GameChatWindow from "$lib/components/GameChatWindow.svelte";
  import LeaveButton from "$lib/components/LeaveButton.svelte";
  
  let avatarUrl = "https://placehold.co/88x88";
  let avatarAlt = "Player avatar";
  
  let characters = Array(32).fill(null).map((_, i) => ({
    id: i,
    name: "ANDRÉ",
    imageUrl: "https://placehold.co/121x166",
    hovered: false,
    clicked: false
  }));
  
  function handleLeaveClick() {
    console.log("Leaving game");
  }
  
  function handleAvatarClick() {
    console.log("Avatar clicked");
  }
  
  const customButtons = [
    {
      component: LeaveButton,
      props: { onClick: handleLeaveClick }
    }
  ];
</script>

<BackgroundLayout>
  <div class="game-container">
    <div class="navbar-wrapper">
      <Navbar 
        avatarUrl={avatarUrl}
        avatarAlt={avatarAlt}
        onAvatarClick={handleAvatarClick}
        customButtons={customButtons}
      />
    </div>
    
    <div class="game-content">
      <div class="game-grid-container">
        <div class="game-grid">
          {#each characters as character (character.id)}
            <CharacterCard 
              characterName={character.name}
              imageUrl={character.imageUrl}
              bind:hovered={character.hovered}
              bind:clicked={character.clicked}
            />
          {/each}
        </div>
      </div>
      
      <div class="chat-container">
        <GameChatWindow currentUsername="YOU" />
      </div>
    </div>
  </div>
</BackgroundLayout>

<style>
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
  
  .chat-container {
    flex: 0 0 700px;
    width: 700px;
    height: 710px;
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
    
    .chat-container {
      margin-top: 20px;
      flex: 0 0 600px;
      width: 600px;
    }
  }
</style>