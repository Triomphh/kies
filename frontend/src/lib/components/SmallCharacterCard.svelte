<script>
  export let characterName = "ANDRÉ";
  export let imageUrl = "/images/andre.png";
</script>

<div class="card_center" style="background: url({imageUrl}) lightgray 50% / cover no-repeat;">
  <div class="frame-5">
    <div class="andr"><span class="andr_span">{characterName}</span></div>
  </div>
</div>

<style>
  .andr_span {
    color: black;
    font-size: 10px;
    font-family: Roboto Mono;
    font-weight: 500;
    word-wrap: break-word;
    user-select: none;
  }

  .andr {
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
  }

  .frame-5 {
    align-self: stretch;
    height: 18px;
    padding: 6px;
    background: white;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    justify-content: center;
    align-items: center;
    gap: 10px;
    display: inline-flex;
  }

  .card_center {
    display: flex;
    width: 85px;
    height: 116px;
    padding: 0px 15px;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    gap: 10px;
    border-radius: 8px;
    border: 4px solid #FFF;
    transition: transform 0.2s ease, z-index 0.01s linear;
    z-index: 1;
  }
  
  .card_center:hover {
    transform: scale(1.1);
    z-index: 10;
  }
</style>