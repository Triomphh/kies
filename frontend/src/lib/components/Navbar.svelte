<script lang="ts">
  import LeaderboardButton from './LeaderboardButton.svelte';
  import GridButton from './GridButton.svelte';
  import AvatarButton from './AvatarButton.svelte';
  
  export let avatarUrl: string = "https://placehold.co/88x88";
  export let avatarAlt: string = "User avatar";
  export let onLeaderboardClick: () => void = () => {};
  export let onGridClick: () => void = () => {};
  export let onAvatarClick: () => void = () => {};
</script>

<div class="navbar">
  <LeaderboardButton onClick={onLeaderboardClick} />
  <GridButton onClick={onGridClick} />
  <AvatarButton imageUrl={avatarUrl} altText={avatarAlt} onClick={onAvatarClick} />
</div>

<style>
  .navbar {
    display: inline-flex;
    justify-content: flex-end;
    align-items: center;
    gap: 30px;
    padding: 20px;
    width: 100%;
    position: fixed;
    top: 0;
    right: 0;
    z-index: 100;
  }
</style>