<script lang="ts">
  export let imageUrl = "https://placehold.co/88x88";
  export let altText = "User avatar";
  export let onClick: (() => void) | null = null;
</script>

<button class="avatar-button" on:click={onClick ? onClick : () => {}}>
  <img class="avatar-image" src={imageUrl} alt={altText} />
</button>

<style>
  .avatar-button {
    width: 88px;
    height: 88px;
    position: relative;
    padding: 0;
    border: none;
    background: none;
    cursor: pointer;
    border-radius: 9999px;
  }

  .avatar-image {
    width: 100%;
    height: 100%;
    border-radius: 9999px;
    border: 4px white solid;
    box-sizing: border-box;
    object-fit: cover;
    object-position: center;
  }

  .avatar-button:hover .avatar-image {
    border: 4px #82BEFF solid;
  }
</style>