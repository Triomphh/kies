<script lang="ts">
  export let onClick: () => void = () => {};
</script>

<button class="register-button" on:click={onClick}>
  <span>Register</span>
</button>

<style>
  .register-button {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 20px;
    background-color: #5C6BC0;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 16px;
  }
  
  .register-button:hover {
    background-color: #3F51B5;
  }
</style> 