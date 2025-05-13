<script lang="ts">
  export let statusMessage: string | null = "Game loading...";
  export let messageType: 'info' | 'win' | 'lose' | 'error' | 'waiting' = 'info';

  export let currentRound: number | undefined = undefined;
  export let maxRounds: number | undefined = undefined;
  export let creatorNickname: string | undefined = undefined;
  export let creatorRoundWins: number | undefined = undefined;
  export let opponentNickname: string | undefined = undefined;
  export let opponentRoundWins: number | undefined = undefined;
  export let gameStatus: string | null | undefined = null;

  $: statusClass = `status-message ${messageType}`;

  let displayMessage = '';
  let scoreDisplay = '';

  $: {
    if (gameStatus === 'IN_PROGRESS' && currentRound !== undefined && maxRounds !== undefined) {
      displayMessage = `Manche: ${currentRound} / ${maxRounds}`;
      if (creatorNickname && creatorRoundWins !== undefined && opponentNickname && opponentRoundWins !== undefined) {
        scoreDisplay = `Score: ${creatorNickname} ${creatorRoundWins} - ${opponentRoundWins} ${opponentNickname}`;
      } else {
        scoreDisplay = '';
      }
    } else if (statusMessage) {
      displayMessage = statusMessage;
      scoreDisplay = '';
    } else {
      displayMessage = "Game status will appear here.";
      scoreDisplay = '';
    }
  }
</script>

<div class={statusClass} role="status" aria-live="polite">
  <p>{displayMessage}</p>
  {#if scoreDisplay}
    <p class="score">{scoreDisplay}</p>
  {/if}
</div>

<style>
  .status-message {
    padding: 12px 18px;
    border-radius: 6px;
    margin-bottom: 15px;
    text-align: center;
    font-size: 1.05em;
    border: 1px solid transparent;
    line-height: 1.4;
  }

  .status-message p {
    margin: 0;
  }
  .status-message p.score {
    font-size: 0.9em;
    margin-top: 4px;
  }

  .status-message.info {
    background-color: #e7f3fe;
    color: #0c5460;
    border-color: #b8daff;
  }

  .status-message.waiting {
    background-color: #fff3cd;
    color: #856404;
    border-color: #ffeeba;
  }
  
  .status-message.win {
    background-color: #d4edda;
    color: #155724;
    border-color: #c3e6cb;
    font-weight: bold;
  }

  .status-message.lose {
    background-color: #f8d7da;
    color: #721c24;
    border-color: #f5c6cb;
    font-weight: bold;
  }

  .status-message.error {
    background-color: #f8d7da;
    color: #721c24;
    border-color: #f5c6cb;
  }
</style>