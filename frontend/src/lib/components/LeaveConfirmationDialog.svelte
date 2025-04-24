<script lang="ts">
  import CloseButton from './CloseButton.svelte';
  import PopupLeaveButton from './PopupLeaveButton.svelte';
  import PopupCancelButton from './PopupCancelButton.svelte';

  export let isVisible: boolean = false;
  export let onClose: () => void = () => {};
  export let onLeave: () => void = () => {};
</script>

{#if isVisible}
  <div class="popup-overlay">
    <div class="confirmation-dialog">
      <div class="close_cross_frame">
        <CloseButton onClick={onClose} />
      </div>
      <div class="message_layout">
        <div class="main_message"><span class="main_message_span">Voulez-vous vraiment quitter ?</span></div>
        <div class="description_message"><span class="description_message_span">Vous perdrez vos modifications</span></div>
      </div>
      <div class="buttons_frame">
        <PopupLeaveButton onClick={onLeave} />
        <PopupCancelButton onClick={onClose} />
      </div>
    </div>
  </div>
{/if}

<style>
  :global(*) {
    box-sizing: border-box;
  }

  .popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .main_message_span {
    color: #4C4C4C;
    font-size: 32px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    word-wrap: break-word;
  }

  .main_message {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .description_message_span {
    color: #4C4C4C;
    font-size: 19px;
    font-family: 'Comic Neue', cursive;
    font-weight: 700;
    line-height: 21.85px;
    word-wrap: break-word;
  }

  .description_message {
    align-self: stretch;
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .message_layout {
    align-self: stretch;
    flex: 1 1 0;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    display: flex;
  }

  .buttons_frame {
    align-self: stretch;
    padding-left: 19px;
    padding-right: 19px;
    justify-content: center;
    align-items: center;
    gap: 16px;
    display: inline-flex;
  }

  .close_cross_frame {
    align-self: stretch;
    height: 35px;
    position: relative;
    display: flex;
    justify-content: flex-end;
  }

  .confirmation-dialog {
    display: flex;
    width: 534px;
    height: 286px;
    padding: 28px 43px;
    flex-direction: column;
    align-items: center;
    gap: 42px;
    border-radius: 17px;
    border: 6px solid #E4E4E4;
    background: #FAFAFA;
    box-shadow: 0px 9px 3px 0px rgba(0, 0, 0, 0.25);
    position: relative;
    overflow: hidden;
  }
</style>