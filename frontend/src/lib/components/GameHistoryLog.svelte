<script context="module" lang="ts">
  export type LogEntry = { id: string | number; timestamp: Date; } & (
    | { type: 'question'; playerId: string; playerName: string; text: string; }
    | { type: 'answer'; playerId: string; playerName: string; questionText: string; answer: boolean; }
    | { type: 'guess'; playerId: string; playerName: string; characterName: string; correct: boolean; }
    | { type: 'event'; message: string; eventClass?: 'system' | 'question' | 'answer' | 'guess' | 'error'; }
  );
</script>

<script lang="ts">
  export let logEntries: LogEntry[] = [];

  function formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
</script>

<div class="game-history-log">
  <h4>Game Log</h4>
  {#if logEntries.length === 0}
    <p class="empty-log">No game events yet.</p>
  {:else}
    <ul>
      {#each logEntries as entry (entry.timestamp.getTime() + Math.random())}
        <li class="log-entry {entry.type}">
          <span class="timestamp">[{formatTime(entry.timestamp)}]</span>
          {#if entry.type === 'question'}
            <span class="player-name">{entry.playerName || entry.playerId}</span> "<em>{entry.text}</em>"
          {:else if entry.type === 'answer'}
            <span class="player-name">{entry.playerName || entry.playerId}</span> a répondu "<strong>{entry.answer ? 'YES' : 'NO'}</strong>" à "<em>{entry.questionText}</em>"
          {:else if entry.type === 'guess'}
            <span class="player-name">{entry.playerName || entry.playerId}</span> a deviné <span class="character-guess">{entry.characterName}</span>. C'était <span class={entry.correct ? 'correct-guess' : 'incorrect-guess'}>{entry.correct ? 'correct' : 'incorrect'}</span>!
          {:else if entry.type === 'event'}
            <span class="event-message">{entry.message}</span>
          {/if}
        </li>
      {/each}
    </ul>
  {/if}
</div>

<style>
  .game-history-log {
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #fdfdfd;
    max-height: 300px;
    overflow-y: auto;
    font-family: 'Arial', sans-serif;
  }

  h4 {
    margin-top: 0;
    margin-bottom: 10px;
    color: #333;
    border-bottom: 1px solid #eee;
    padding-bottom: 5px;
  }

  .empty-log {
    color: #777;
    font-style: italic;
  }

  ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
  }

  .log-entry {
    padding: 6px 0;
    border-bottom: 1px dashed #eee;
    font-size: 0.9em;
    line-height: 1.4;
  }

  .log-entry:last-child {
    border-bottom: none;
  }

  .timestamp {
    color: #888;
    margin-right: 8px;
    font-size: 0.85em;
  }

  .player-name {
    font-weight: bold;
    color: #007bff;
  }

  .log-entry.question .player-name { color: #17a2b8; }
  .log-entry.answer .player-name { color: #28a745; }
  .log-entry.guess .player-name { color: #ffc107; }


  em {
    color: #555;
  }

  strong {
    font-weight: bold;
  }

  .log-entry.answer strong {
    color: #28a745;
  }
  .log-entry.answer strong:before {
    content: '';
  }
   .log-entry.answer span:nth-of-type(2) {
  }


  .character-guess {
    font-style: italic;
    color: #6f42c1;
  }

  .correct-guess {
    color: #28a745;
    font-weight: bold;
  }

  .incorrect-guess {
    color: #dc3545;
    font-weight: bold;
  }
  
  .event-message {
    color: #6c757d;
    font-style: italic;
  }
</style>