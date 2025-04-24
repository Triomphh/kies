<script>
</script>

<div class="grid-add-button">
  <div class="icon-wrapper">
    <svg width="53" height="55" viewBox="0 0 53 55" fill="none" xmlns="http://www.w3.org/2000/svg" class="default-state">
      <path d="M3.5 27.5H49.5ZM26.5 4V51Z" fill="white"/>
      <path d="M3.5 27.5H49.5M26.5 4V51" stroke="white" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
    <svg width="55" height="55" viewBox="0 0 55 55" fill="none" xmlns="http://www.w3.org/2000/svg" class="hover-state">
      <path d="M4.5 27.5H50.5ZM27.5 4V51Z" fill="white"/>
      <path d="M4.5 27.5H50.5M27.5 4V51" stroke="white" stroke-width="8" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  </div>
</div>

<style>
  .grid-add-button {
    width: 461px;
    height: 161px;
    padding: 10px;
    border-radius: 15px;
    justify-content: center;
    align-items: center;
    display: inline-flex;
    cursor: pointer;
  }
  
  .grid-add-button:hover {
    background: rgba(255, 255, 255, 0.24);
    outline: 5px rgba(255, 255, 255, 0.29) solid;
    outline-offset: -5px;
  }
  
  .icon-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
  }
  
  .default-state {
    display: block;
  }
  
  .hover-state {
    display: none;
    position: absolute;
  }
  
  .grid-add-button:hover .default-state {
    display: none;
  }
  
  .grid-add-button:hover .hover-state {
    display: block;
  }
</style>