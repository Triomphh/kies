<script>
  import CharacterCard from '$lib/components/CharacterCard.svelte';
</script>

<main>
  <h1>Kies</h1>
  
  <div class="card-container">
    <CharacterCard 
      characterName="ANDRÉ" 
      imageUrl="/images/andre.png" 
    />
  </div>
  
  <p>Démo d'une carte d'un personnage</p>
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
    margin: 2em 0;
  }
</style>
