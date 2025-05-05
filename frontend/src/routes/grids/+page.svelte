<script lang="ts">
  import BackgroundLayout from "$lib/components/BackgroundLayout.svelte";
  import Navbar from "$lib/components/Navbar.svelte";
  import GridPreviewButton from "$lib/components/GridPreviewButton.svelte";
  import GridAddButton from "$lib/components/GridAddButton.svelte";
  import BackButton from "$lib/components/BackButton.svelte";
  import LeaderboardButton from "$lib/components/LeaderboardButton.svelte";
  import { goto } from "$app/navigation";
  
  const grids = [
    {
      id: 1,
      name: "GRID NAME",
      author: "OFFICIELLE",
      isOfficial: true,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 2,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 3,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 4,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 5,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 6,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 7,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 8,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    },
    {
      id: 9,
      name: "GRID NAME",
      author: "Jackie36",
      isOfficial: false,
      characters: [
        { name: "MACRON", imageUrl: "/images/macron.jpeg" },
        { name: "ANDRÉ", imageUrl: "/images/andre.png" },
        { name: "PABLO", imageUrl: "/images/cop.png" }
      ]
    }
  ];

  function handleLeaderboardClick() {
    console.log('Navigate to leaderboard');
  }

  function handleBackClick() {
    goto('/');
  }

  function handleAvatarClick() {
    console.log('Open avatar/profile menu');
  }

  function handleAddGridClick() {
    goto('/grids/add');
  }

  function handleGridClick(gridId: number) {
    console.log(`Grid ${gridId} clicked`);
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
  <div class="grids-page">
    <Navbar
      showLeaderboardButton={false}
      showGridButton={false}
      customButtons={customNavButtons}
      avatarUrl="https://placehold.co/88x88"
      avatarAlt="User avatar"
      onAvatarClick={handleAvatarClick}
    />
    
    <div class="grids-container">
      <div on:click={handleAddGridClick} on:keydown={(e) => e.key === 'Enter' && handleAddGridClick()} role="button" tabindex="0">
        <GridAddButton />
      </div>
      
      {#each grids as grid (grid.id)}
        <div on:click={() => handleGridClick(grid.id)} on:keydown={(e) => e.key === 'Enter' && handleGridClick(grid.id)} role="button" tabindex="0">
          <GridPreviewButton 
            gridName={grid.name}
            author={grid.author}
            isOfficial={grid.isOfficial}
            characters={grid.characters}
          />
        </div>
      {/each}
    </div>
  </div>
</BackgroundLayout>

<style>
  .grids-page {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .grids-container {
    max-width: 1620px;
    padding: 120px 20px 20px 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 70px;
    justify-content: flex-start;
    align-items: flex-start;
  }

  .grids-container > div {
    cursor: pointer;
  }
</style>