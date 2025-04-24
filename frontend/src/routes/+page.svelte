<script>
  import CharacterCard from '$lib/components/CharacterCard.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import AddCard from '$lib/components/AddCard.svelte';
  import BackButton from '$lib/components/BackButton.svelte';
  import LeaveButton from '$lib/components/LeaveButton.svelte';
  import LeaderboardButton from '$lib/components/LeaderboardButton.svelte';
  import OkButton from '$lib/components/OkButton.svelte';
  import CloseButton from '$lib/components/CloseButton.svelte';
  import PopupLeaveButton from '$lib/components/PopupLeaveButton.svelte';
  import PopupCancelButton from '$lib/components/PopupCancelButton.svelte';
  import LeaveConfirmationDialog from '$lib/components/LeaveConfirmationDialog.svelte';
  
  let showLeaveConfirmation = false;
  
  function toggleLeaveConfirmation() {
    showLeaveConfirmation = !showLeaveConfirmation;
  }
  
  function handleLeave() {
    showLeaveConfirmation = false;
    console.log('User decided to leave');
  }
</script>

<main>
  <h1>Kies</h1>
  
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
  </div>

  <p>Démo des boutons</p>
  
  <div class="spacer"></div>

  <LeaveButton onClick={toggleLeaveConfirmation} />
  
  <LeaveConfirmationDialog 
    isVisible={showLeaveConfirmation} 
    onClose={toggleLeaveConfirmation} 
    onLeave={handleLeave} 
  />

  <p>Démo popups</p>

</main>

<style>
  :global(body) {
    background-color: #BDDFFF;
    margin: 0;
    padding: 0;
  }

  main {
    text-align: center;
    padding: 1em;
    max-width: 800px;
    margin: 0 auto;
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
  
</style>
