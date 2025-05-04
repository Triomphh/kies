<script>
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  import GameSessionCard from '$lib/components/GameSessionCard.svelte';
  import LeaderboardButton from '$lib/components/LeaderboardButton.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import { goto } from '$app/navigation';
  
  const emptyGameSessions = [
    { player1Name: "JACOB", player1Image: "/images/cop.png" },
    { player1Name: "JACOB", player1Image: "/images/cop.png" },
    { player1Name: "JACOB", player1Image: "/images/cop.png" }
  ];
  
  const fullGameSessions = [
    { 
      player1Name: "JACOB", 
      player1Image: "/images/cop.png",
      player2Name: "JOJO",
      player2Image: "/images/andre.png"
    },
    { 
      player1Name: "JACOB", 
      player1Image: "/images/cop.png",
      player2Name: "JOJO",
      player2Image: "/images/andre.png"
    }
  ];
  
  // Event handlers
  const handleJoinGame = () => {
    console.log("Join game clicked");
  };
  
  const handleSpectateGame = () => {
    console.log("Spectate game clicked");
  };
  
  const handleLeaderboardClick = () => {
    console.log("Leaderboard clicked");
  };
  
  const handleGridClick = () => {
    goto('/grids');
  };
  
  const handleAvatarClick = () => {
    console.log("Avatar clicked");
  };
  
  const customButtons = [
    {
      component: LeaderboardButton,
      props: { onClick: handleLeaderboardClick }
    },
    {
      component: GridButton,
      props: { onClick: handleGridClick }
    }
  ];
</script>

<BackgroundLayout>
  <div class="home-container">
    <Navbar 
      avatarUrl="https://placehold.co/88x88" 
      avatarAlt="User avatar" 
      onAvatarClick={handleAvatarClick}
      customButtons={customButtons}
    />
    
    <div class="content-container">
      <div class="game-sessions-container">
        <div class="game-sessions-grid">
          {#each emptyGameSessions as session}
            <GameSessionCard 
              player1Name={session.player1Name} 
              player1Image={session.player1Image} 
              isFull={false} 
              onJoin={handleJoinGame} 
            />
          {/each}
          
          {#each fullGameSessions as session}
            <GameSessionCard 
              player1Name={session.player1Name} 
              player1Image={session.player1Image}
              player2Name={session.player2Name}
              player2Image={session.player2Image}
              isFull={true}
              onSpectate={handleSpectateGame}
            />
          {/each}
        </div>
      </div>
      
      <div class="divider">
        <span>OU</span>
      </div>
      
      <div class="grid-info-panel">
        
      </div>
    </div>
  </div>
</BackgroundLayout>

<style>
  .home-container {
    width: 100%;
    height: 100%;
    padding: 20px;
    box-sizing: border-box;
  }
  
  .content-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 100px;
    gap: 48px;
  }
  
  .game-sessions-container {
    max-width: 956px;
  }
  
  .game-sessions-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 124px;
    justify-content: flex-start;
    align-content: flex-start;
  }
  
  .divider {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    align-self: center;
  }
  
  .divider span {
    color: white;
    font-size: 32px;
    font-family: Comic Neue, sans-serif;
    font-weight: 700;
    text-align: center;
  }
  
  .grid-info-panel {
    width: 526px;
    height: 818px;
    background: linear-gradient(0deg, white 0%, white 100%), 
                linear-gradient(133deg, rgba(255, 255, 157.79, 0.52) 0%, rgba(249.31, 142.29, 240.39, 0.19) 100%);
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
    border-radius: 7px;
    backdrop-filter: blur(5px);
  }
</style>
