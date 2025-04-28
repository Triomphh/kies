<script lang="ts">
  export let placeholder: string = "Écris ton message...";
  export let value: string = "";
  export let onInput: (event: Event) => void = () => {};
  export let onSubmit: () => void = () => {};

  function handleKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      onSubmit();
    }
  }
</script>

<input 
  type="text" 
  class="chat-input" 
  placeholder={placeholder}
  bind:value={value}
  on:input={onInput}
  on:keydown={handleKeydown}
/>

<style>
  .chat-input {
    width: 100%;
    height: 73px;
    padding-left: 31px;
    padding-right: 31px;
    padding-top: 22px;
    padding-bottom: 22px;
    background: white;
    border-radius: 6px;
    border: none;
    outline: 3px #E4E4E4 solid;
    outline-offset: -3px;
    box-sizing: border-box;
    
    color: black;
    font-size: 24px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    cursor: text;
  }

  .chat-input::placeholder {
    color: rgba(0, 0, 0, 0.25);
    font-size: 24px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    word-wrap: break-word;
  }
</style>