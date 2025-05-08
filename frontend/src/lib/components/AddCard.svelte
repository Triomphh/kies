<script lang="ts">
  export let hovered: boolean = false;
  export let onClick: (name: string, imageFile?: File) => void = () => {};
  export let imageUrl: string = '';
  export let name: string = '';
  
  let fileInput: HTMLInputElement;
  let selectedFile: File | null = null;

  function handleNameChange(event: Event) {
    const target = event.target as HTMLInputElement;
    name = target.value;
  }
  
  function handleNameBlur() {
    // Only notify parent when user finishes typing (on blur)
    onClick(name, selectedFile || undefined);
  }

  function handleCardKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      fileInput.click();
    }
  }

  function stopPropagation(event: Event) {
    event.stopPropagation();
  }
  
  function handleFileSelect() {
    fileInput.click();
  }
  
  function handleFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      selectedFile = target.files[0];
      // Create a temporary URL for the image
      const tempUrl = URL.createObjectURL(selectedFile);
      imageUrl = tempUrl;
      onClick(name, selectedFile);
    }
  }
</script>

<div 
  class="card" 
  class:hovered={hovered}
  class:has-image={imageUrl}
  style={imageUrl ? `background-image: url(${imageUrl});` : ''}
  on:mouseenter={() => hovered = true}
  on:mouseleave={() => hovered = false}
  on:click={handleFileSelect}
  on:keydown={handleCardKeyPress}
  role="button"
  tabindex="0"
  aria-label="Add a new character"
>
  <input 
    type="file" 
    accept="image/*" 
    style="display: none;" 
    bind:this={fileInput}
    on:change={handleFileChange}
  />
  
  {#if !imageUrl}
    <div data-svg-wrapper>
      <svg width="33" height="32" viewBox="0 0 33 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M2.5 16H30.5M16.5 2V30" stroke="black" stroke-opacity="0.41" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
  {/if}
  
  <div class="frame-5">
    <input 
      type="text" 
      class="character-name-input" 
      placeholder="Nom"
      bind:value={name}
      on:click={stopPropagation}
      on:input={handleNameChange}
      on:blur={handleNameBlur}
    />
  </div>
</div>

<style>
  .card {
    display: inline-flex;
    width: 121px;
    height: 166px;
    padding: 5px 15px 5px 15px;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    gap: 44px;
    flex-shrink: 0;
    border-radius: 8px;
    background: rgba(196, 196, 196, 0.40);
    outline: 5px rgba(255, 255, 255, 0.40) solid;
    outline-offset: -5px;
    box-sizing: border-box;
    box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.25);
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
  }

  .card.hovered {
    background-color: rgba(196, 196, 196, 0.50);
    outline: 5px rgba(255, 255, 255, 0.50) solid;
  }
  
  .card.has-image {
    gap: 10px;
    outline: 5px rgba(255, 255, 255, 1) solid; /* Fully opaque white border */
  }
  
  .card.has-image .frame-5 {
    background: rgba(255, 255, 255, 1); /* Fully opaque white background for name area */
  }

  .character-name-input {
    width: 76px;
    height: 21px;
    background: rgba(255, 255, 255, 0.70);
    border: none;
    text-align: center;
    font-family: 'Roboto Mono', monospace;
    font-size: 14px;
    font-weight: 500;
  }

  .character-name-input:focus {
    outline: none;
  }

  .frame-5 {
    align-self: stretch;
    height: 27px;
    padding-top: 6px;
    padding-left: 6px;
    padding-right: 6px;
    background: rgba(255, 255, 255, 0.40);
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: inline-flex;
  }

  [data-svg-wrapper] {
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>