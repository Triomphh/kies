<script lang="ts">
  import { onMount } from 'svelte';
  import { playerService } from '$lib/services/playerService';
  import BackgroundLayout from '$lib/components/BackgroundLayout.svelte';
  import BackButton from '$lib/components/BackButton.svelte';
  import Navbar from '$lib/components/Navbar.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import { goto } from '$app/navigation';
  import { authStore } from '$lib/services/authService';
  import { fly } from 'svelte/transition';
  import { quintOut } from 'svelte/easing';
  
  interface Player {
    playerId: number;
    nickname: string;
    gamesPlayed: number;
    victories: number;
    profileImageUrl?: string;
  }

  let players: Player[] = [];
  let loading = true;
  let error = false;
  let ready = false;

  onMount(async () => {
    try {
      players = await playerService.getLeaderboard();
      loading = false;
      setTimeout(() => {
        ready = true;
      }, 100);
    } catch (err) {
      console.error('Failed to load leaderboard data:', err);
      error = true;
      loading = false;
    }
  });

  function getPositionStyle(index: number) {
    if (index === 0) return 'second-place';
    if (index === 1) return 'first-place';
    if (index === 2) return 'third-place';
    return '';
  }

  function getMedalEmoji(index: number) {
    if (index === 1) return '🥇';
    if (index === 0) return '🥈';
    if (index === 2) return '🥉';
    return `${index + 1}`;
  }

  function handleBackButtonClick() {
    goto('/');
  }
  
  function handleGridClick() {
    goto('/grids');
  }
  
  const customNavButtons = [
    {
      component: BackButton,
      props: { onClick: handleBackButtonClick }
    },
    {
      component: GridButton,
      props: { onClick: handleGridClick }
    }
  ];
  
  function getPlayerAvatarUrl(player: Player): string {
    return player.profileImageUrl || `https://api.dicebear.com/6.x/personas/png?seed=${player.nickname}&backgroundColor=b6e3f4&size=128`;
  }
</script>

<BackgroundLayout showFireworks={true}>
  <Navbar
    showLeaderboardButton={false}
    showGridButton={false}
    customButtons={customNavButtons}
  />
  
  <div class="leaderboard-container">

    {#if loading}
      <div class="loading-container">
        <div class="spinner"></div>
        <p>Chargement du classement...</p>
      </div>
    {:else if error}
      <div class="error-container">
        <p>Une erreur est survenue lors du chargement du classement.</p>
        <button class="retry-button" on:click={() => window.location.reload()}>Réessayer</button>
      </div>
    {:else if players.length === 0}
      <div class="empty-container">
        <p>Aucun joueur dans le classement pour le moment.</p>
      </div>
    {:else}
      {#if ready}
        <div in:fly="{{ y: 500, duration: 2000, easing: quintOut }}">
          <div class="podium-section">
            <div class="podium-container">
              {#if players.length > 0}
                {#each players.slice(0, Math.min(3, players.length)) as player, index}
                  <div class="podium-player {getPositionStyle(index)}">
                    <div class="avatar-container">
                      <img src={getPlayerAvatarUrl(player)} alt="{player.nickname}" class="player-avatar" />
                      <div class="player-medal">{getMedalEmoji(index)}</div>
                    </div>
                    <div class="player-info">
                      <h3 class="player-name">{player.nickname}</h3>
                      <p class="player-victories">{player.victories} {player.victories > 1 ? 'victoires' : 'victoire'}</p>
                    </div>
                  </div>
                {/each}
              {/if}
            </div>
          </div>

          {#if players.length > 3}
            <div class="other-players-container">
              <h2 class="other-players-title">Autres joueurs</h2>
              <div class="players-list">
                {#each players.slice(3) as player, index}
                  <div class="player-card">
                    <div class="player-rank">{index + 4}</div>
                    <div class="player-avatar-small">
                      <img src={getPlayerAvatarUrl(player)} alt="{player.nickname}" />
                    </div>
                    <div class="player-details">
                      <span class="player-name">{player.nickname}</span>
                      <span class="player-stats">{player.victories} {player.victories > 1 ? 'victoires' : 'victoire'} / {player.gamesPlayed} {player.gamesPlayed > 1 ? 'parties' : 'partie'}</span>
                    </div>
                  </div>
                {/each}
              </div>
            </div>
          {/if}
        </div>
      {/if}
    {/if}
  </div>
</BackgroundLayout>

<style>
  .leaderboard-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    padding-top: 6rem;
    color: #4C4C4C;
  }
  
  .back-button-container {
    display: flex;
    margin-bottom: 2rem;
  }

  .loading-container,
  .error-container,
  .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
    text-align: center;
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    color: #4C4C4C;
    font-family: 'Comic Neue', sans-serif;
  }

  .loading-container p,
  .error-container p,
  .empty-container p {
    font-size: 1.2rem;
    font-weight: bold;
  }

  .spinner {
    width: 50px;
    height: 50px;
    border: 5px solid rgba(76, 76, 76, 0.3);
    border-radius: 50%;
    border-top-color: #4C4C4C;
    margin-bottom: 1rem;
  }

  .retry-button {
    margin-top: 1rem;
    padding: 0.6rem 1.2rem;
    background-color: #FFFFFF;
    color: #4C4C4C;
    border: none;
    border-radius: 7px;
    cursor: pointer;
    font-weight: bold;
    font-family: 'Comic Neue', sans-serif;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
  }

  .retry-button:hover {
    background-color: #F4F4F4;
  }
  
  .retry-button:active {
    background-color: #F4F4F4;
    box-shadow: 0px 4px 0px #BDDFFF inset, 0px 4px 0px #F4F4F4;
  }

  .podium-section {
    margin-top: 2.5rem;
    margin-bottom: 3rem;
    background-color: transparent;
    border-radius: 12px;
    padding: 1.5rem;
    position: relative;
    min-height: 500px;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
  }

  .podium-container {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    position: relative;
    margin: 0 auto;
    max-width: 800px;
    height: 330px;
  }

  .podium-player {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    width: 180px;
    margin: 0 5px;
  }

  .first-place {
    order: 2;
    z-index: 3;
    margin-bottom: 140px;
  }

  .second-place {
    order: 1;
    z-index: 2;
    margin-bottom: 80px;
  }

  .third-place {
    order: 3;
    z-index: 1;
    margin-bottom: 40px;
  }
  
  .podium-container::before {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 0;
    right: 0;
    height: 20px;
    background-color: #EFEFEF;
    border-radius: 12px 12px 12px 12px;
    z-index: 0;
  }
  
  .first-place::after {
    content: '1';
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    bottom: -140px;
    left: 0;
    width: 100%;
    height: 140px;
    background: linear-gradient(to bottom, #FFD700, #FFC800);
    border-radius: 12px 12px 0 0;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.25);
    z-index: -1;
    font-weight: bold;
    color: white;
    text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
    font-size: 24px;
  }
  
  .second-place::after {
    content: '2';
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    bottom: -80px;
    left: 0;
    width: 100%;
    height: 80px;
    background: linear-gradient(to bottom, #C0C0C0, #A9A9A9);
    border-radius: 12px 12px 0 0;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.25);
    z-index: -1;
    font-weight: bold;
    color: white;
    text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
    font-size: 24px;
  }
  
  .third-place::after {
    content: '3';
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    bottom: -40px;
    left: 0;
    width: 100%;
    height: 40px;
    background: linear-gradient(to bottom, #CD7F32, #B8732D);
    border-radius: 12px 12px 0 0;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.25);
    z-index: -1;
    font-weight: bold;
    color: white;
    text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
    font-size: 24px;
  }

  .avatar-container {
    position: relative;
    margin-bottom: 1rem;
    z-index: 10;
  }

  .player-avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    border: 4px solid white;
    box-shadow: 0 4px 0px rgba(0, 0, 0, 0.25);
    background-color: #ffffff;
  }

  .first-place .player-avatar {
    width: 150px;
    height: 150px;
    border: 6px solid white;
  }
  
  .second-place .player-avatar {
    border: 4px solid #C0C0C0;
  }
  
  .third-place .player-avatar {
    border: 4px solid #CD7F32;
  }

  .player-medal {
    position: absolute;
    bottom: -5px;
    right: -5px;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    box-shadow: 0 4px 0px rgba(0, 0, 0, 0.25);
    z-index: 11;
  }

  .first-place .player-medal {
    width: 50px;
    height: 50px;
    font-size: 2rem;
  }

  .player-info {
    background-color: #FFFFFF;
    padding: 1rem;
    border-radius: 8px;
    text-align: center;
    width: 100%;
    box-shadow: 0 4px 0px rgba(0, 0, 0, 0.25);
    z-index: 10;
    position: relative;
  }

  .first-place .player-info {
    border: 2px solid #FFD700;
  }

  .second-place .player-info {
    border: 2px solid #C0C0C0;
  }

  .third-place .player-info {
    border: 2px solid #CD7F32;
  }

  .player-name {
    font-weight: bold;
    margin-bottom: 0.5rem;
    font-size: 1.2rem;
    font-family: 'Comic Neue', sans-serif;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 150px;
    color: #4C4C4C;
  }

  .first-place .player-name {
    font-size: 1.5rem;
  }

  .player-victories {
    font-size: 1rem;
    font-family: 'Comic Neue', sans-serif;
    color: #4C4C4C;
  }

  .other-players-container {
    margin-top: 2rem;
  }

  .other-players-title {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    padding-bottom: 0.5rem;
    font-family: 'Comic Neue', sans-serif;
    color: #FFFFFF;
    text-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
    font-weight: bold;
    text-align: center;
  }

  .players-list {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
    max-width: 800px;
    margin: 0 auto;
  }

  .player-card {
    display: flex;
    align-items: center;
    background-color: #FFFFFF;
    padding: 0.7rem 0.9rem;
    border-radius: 8px;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    transition: transform 0.2s;
  }

  .player-card:hover {
    transform: translateX(5px);
  }

  .player-rank {
    font-weight: bold;
    font-size: 1.2rem;
    width: 30px;
    text-align: center;
    font-family: 'Comic Neue', sans-serif;
    color: #4C4C4C;
  }

  .player-avatar-small {
    width: 45px;
    height: 45px;
    margin: 0 0.9rem;
  }

  .player-avatar-small img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid #ffffff;
    box-shadow: 0 2px 0px rgba(0, 0, 0, 0.25);
  }

  .player-details {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    font-size: 0.95rem;
  }

  .player-stats {
    font-size: 0.8rem;
    color: #777777;
  }

  @media (max-width: 768px) {
    .podium-section {
      min-height: 450px;
    }
    
    .podium-container {
      height: 250px;
    }

    .podium-player {
      width: 110px;
      margin: 0 2px;
    }

    .player-avatar {
      width: 80px;
      height: 80px;
    }

    .first-place .player-avatar {
      width: 100px;
      height: 100px;
    }

    .first-place {
      margin-bottom: 100px;
    }

    .second-place {
      margin-bottom: 60px;
    }

    .third-place {
      margin-bottom: 30px;
    }

    .first-place::after {
      bottom: -100px;
      height: 100px;
      font-size: 18px;
    }
    
    .second-place::after {
      bottom: -60px;
      height: 60px;
      font-size: 18px;
    }
    
    .third-place::after {
      bottom: -30px;
      height: 30px;
      font-size: 18px;
    }

    .player-medal {
      width: 30px;
      height: 30px;
      font-size: 1.2rem;
    }

    .first-place .player-medal {
      width: 40px;
      height: 40px;
      font-size: 1.5rem;
    }

    .player-name {
      font-size: 1rem;
      max-width: 100px;
    }

    .first-place .player-name {
      font-size: 1.2rem;
    }
  }

  @media (max-width: 480px) {
    .leaderboard-container {
      padding: 1rem;
    }

    .podium-container {
      flex-wrap: wrap;
      height: auto;
      gap: 1.5rem;
    }

    .podium-player {
      width: 100px;
      height: auto !important;
    }

    .first-place, .second-place, .third-place {
      order: initial;
    }

    .first-place {
      order: 1;
    }

    .second-place {
      order: 2;
    }

    .third-place {
      order: 3;
    }

    .player-avatar {
      width: 70px;
      height: 70px;
    }

    .first-place .player-avatar {
      width: 90px;
      height: 90px;
    }

    .player-name {
      font-size: 0.9rem;
      max-width: 80px;
    }

    .player-info {
      padding: 0.7rem;
    }
  }
</style>
