<script>
  import CharacterCard from '$lib/components/CharacterCard.svelte';
  import GridButton from '$lib/components/GridButton.svelte';
  import AddCard from '$lib/components/AddCard.svelte';
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
  
  <div class="button-container">
    <GridButton />
  </div>
</main>

<style>
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
  
  .button-container {
    margin-top: 2em;
    display: flex;
    justify-content: center;
  }
</style>
