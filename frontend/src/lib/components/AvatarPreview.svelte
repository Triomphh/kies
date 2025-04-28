<script>
  export let imageUrl = "https://placehold.co/88x88";
  export let altText = "User avatar";
  export let borderColor = "white";
</script>

<div class="avatar-preview">
  <img 
    class="avatar-image" 
    src={imageUrl} 
    alt={altText} 
    style="border-color: {borderColor};"
  />
</div>

<style>
  .avatar-preview {
    width: 88px;
    height: 88px;
    position: relative;
    border-radius: 9999px;
  }

  .avatar-image {
    width: 100%;
    height: 100%;
    border-radius: 9999px;
    border: 4px solid;
    box-sizing: border-box;
    object-fit: cover;
    object-position: center;
  }
</style>