<script>
  import CharacterCard from '$lib/components/CharacterCard.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import GridPreviewButton from '$lib/components/GridPreviewButton.svelte';
  import GridAddButton from '$lib/components/GridAddButton.svelte';
  import AddCard from '$lib/components/AddCard.svelte';
  import BackButton from '$lib/components/BackButton.svelte';
  import LeaveButton from '$lib/components/LeaveButton.svelte';
  import LeaderboardButton from '$lib/components/LeaderboardButton.svelte';
  import OkButton from '$lib/components/OkButton.svelte';
  import CloseButton from '$lib/components/CloseButton.svelte';
  import PopupLeaveButton from '$lib/components/PopupLeaveButton.svelte';
  import PopupCancelButton from '$lib/components/PopupCancelButton.svelte';
  import LeaveConfirmationDialog from '$lib/components/LeaveConfirmationDialog.svelte';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import GameSessionCard from '$lib/components/GameSessionCard.svelte';
  import GameChatWindow from '$lib/components/GameChatWindow.svelte';
  import ChooseButton from '$lib/components/ChooseButton.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  
  let showLeaveConfirmation = false;
  let chatMessage = "";
  let isChooseActive = false;
  
  let chatMessages = [
    {
      username: "JACOB",
      avatarSrc: "/images/cop.png",
      message: "salut mon caillou",
      isCurrentUser: true
    },
    {
      username: "JOJO",
      avatarSrc: "/images/andre.png",
      message: "pk je suis sur les images??",
      isCurrentUser: false
    }
  ];
  
  function toggleLeaveConfirmation() {
    showLeaveConfirmation = !showLeaveConfirmation;
  }
  
  function handleLeave() {
    showLeaveConfirmation = false;
    console.log('LEAVE - GAME');
  }
  
  function handleJoinGame() {
    console.log('JOIN - GAME SESSION');
  }
  
  function handleSpectateGame() {
    console.log('SPECTATE - GAME SESSION');
  }
  
  function toggleChooseButton() {
    isChooseActive = !isChooseActive;
    console.log(`ChooseButton is now ${isChooseActive ? 'active' : 'inactive'}`);
  }
  
  function handleLeaderboardClick() {
    console.log('Leaderboard button clicked');
  }
  
  function handleGridClick() {
    console.log('Grid button clicked');
  }
  
  function handleAvatarClick() {
    console.log('Avatar button clicked');
  }
</script>

<BackgroundLayout>
  <div class="app-container">
    <Navbar 
      avatarUrl="/images/cop.png"
      avatarAlt="Player avatar"
      onLeaderboardClick={handleLeaderboardClick}
      onGridClick={handleGridClick}
      onAvatarClick={handleAvatarClick}
    />
    <main>
      <h1>Kies</h1>
      
      <div class="spacer navbar-spacer"></div>
      
      <div class="card-container">
        <CharacterCard 
          characterName="ANDRÉ" 
          imageUrl="/images/andre.png" 
        />
        <AddCard />
      </div>
      
      <p>Démo d'une carte d'un personnage</p>
      
      <div class="spacer"></div>

      <div class="button-container">
        <GridButton />
        <LeaderboardButton />
        <BackButton />
        <LeaveButton />
        <OkButton />
        <CloseButton />
        <PopupLeaveButton />
        <PopupCancelButton />
        <ChooseButton isActive={isChooseActive} onClick={toggleChooseButton} />
      </div>

      <p>Démo des boutons</p>
      
      <div class="spacer"></div>

      <div class="grid-preview-container">
        <GridPreviewButton 
          gridName="GRID NAME"
          author="Jackie36"
          characters={[
            { name: "MACRON", imageUrl: "/images/macron.jpeg" },
            { name: "ANDRÉ", imageUrl: "/images/andre.png" },
            { name: "PABLO", imageUrl: "/images/cop.png" }
          ]}
        />
      </div>

      <div class="grid-add-container">
        <GridAddButton />
      </div>

      <p>Grids preview</p>

      <div class="spacer"></div>

      <LeaveButton onClick={toggleLeaveConfirmation} />
      
      <LeaveConfirmationDialog 
        isVisible={showLeaveConfirmation} 
        onClose={toggleLeaveConfirmation} 
        onLeave={handleLeave} 
      />

      <p>Démo popups</p>

      <div class="spacer"></div>

      <div class="game-session-container">
        <GameSessionCard 
          player1Name="JACOB"
          player1Image="/images/cop.png"
          isFull={false}
          onJoin={handleJoinGame}
        />
        
        <GameSessionCard 
          player1Name="JACOB"
          player1Image="/images/cop.png"
          player2Name="JOJO"
          player2Image="/images/andre.png"
          isFull={true}
          onSpectate={handleSpectateGame}
        />
      </div>

      <p>Démo preview de partie ouverte ou en cours</p>

      <div class="spacer"></div>
      
      <div class="chat-container">
        <GameChatWindow 
          messages={chatMessages}
          currentUsername="JACOB"
        />
      </div>
      
      <p>Démo du chat</p>

    </main>
  </div>
</BackgroundLayout>

<style>
  .app-container {
    position: relative;
    width: 100%;
    min-height: 100vh;
  }

  .navbar-spacer {
    height: 120px; /* Provides space for the fixed navbar */
  }

  main {
    text-align: center;
    padding: 1em;
    max-width: 800px;
    margin: 0 auto;
    padding-top: 20px;
  }

  h1 {
    color: #ff3e00;
    font-size: 2em;
    font-weight: 100;
    margin-bottom: 1em;
  }

  .card-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 2em 0;
  }
  
  .spacer {
    height: 40px;
  }
  
  .button-container {
    margin-top: 2em;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .grid-preview-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 2em 0;
  }
  
  .grid-add-container {
    display: flex;
    justify-content: center;
    margin: 1em 0;
  }
  
  .game-session-container {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 30px;
    margin: 2em 0;
  }
  
  .chat-container {
    display: flex;
    justify-content: center;
    margin: 2em 0;
  }
  </style>
