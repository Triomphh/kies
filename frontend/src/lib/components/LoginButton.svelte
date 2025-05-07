<script lang="ts">
  export let onClick: () => void = () => {};
</script>

<button class="login-button" on:click={onClick}>
  <span>Login</span>
</button>

<style>
  .login-button {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 20px;
    background-color: transparent;
    color: white;
    border: 2px solid white;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 16px;
  }
  
  .login-button:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
</style> 