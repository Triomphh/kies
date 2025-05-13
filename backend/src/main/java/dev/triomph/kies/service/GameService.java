package dev.triomph.kies.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.triomph.kies.dto.GameDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.triomph.kies.DAO.GameDAO;
import dev.triomph.kies.DAO.PlayerDAO;
import dev.triomph.kies.DAO.QuestionDAO;
import dev.triomph.kies.DAO.CharacterDAO;
import dev.triomph.kies.pojo.Answer;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import dev.triomph.kies.pojo.Round;
import dev.triomph.kies.pojo.Character;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class GameService {

    private final GameDAO gameDAO;
    private final PlayerDAO playerDAO;
    private final QuestionDAO questionDAO;
    private final CharacterDAO characterDAO;
    private final ObjectMapper objectMapper;
    private final PlayerService playerService;

    public GameService(GameDAO gameDAO, PlayerDAO playerDAO, QuestionDAO questionDAO, CharacterDAO characterDAO, ObjectMapper objectMapper, PlayerService playerService) {
        this.gameDAO = gameDAO;
        this.playerDAO = playerDAO;
        this.questionDAO = questionDAO;
        this.characterDAO = characterDAO;
        this.objectMapper = objectMapper;
        this.playerService = playerService;
    }

    public List<Game> getAllGames() {
        return gameDAO.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameDAO.findById(id);
    }
    
    private Game getGameOrThrow(Long gameId) {
        return gameDAO.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));
    }

    private Player getPlayerOrThrow(Long playerId) {
        return playerDAO.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }
    
    private Question getQuestionOrThrow(Long questionId) {
        return questionDAO.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
    }

    private Character getCharacterOrThrow(Long characterId) {
        return characterDAO.findById(characterId)
            .orElseThrow(() -> new IllegalArgumentException("Character not found with ID: " + characterId));
    }


    public Game createGame(Player creator, Integer maxRounds, Integer turnLimit, Grid grid, String password, Boolean allowSpectators) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator (Player) cannot be null when creating a game.");
        }
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null when creating a game.");
        }

        Game game = new Game(creator, null, maxRounds, turnLimit, grid);
        game.setPassword(password);
        game.setAllowSpectators(allowSpectators != null ? allowSpectators : true);
        game.setStatus(Game.GameStatus.WAITING);

        return gameDAO.save(game);
    }
    
    @Transactional
    public Game joinGame(Long gameId, Player opponentPlayer) {
        Game game = getGameOrThrow(gameId);
        if (game.getOpponent() != null) {
            throw new IllegalStateException("Game already has an opponent.");
        }
        if (game.getCreator().getPlayerId().equals(opponentPlayer.getPlayerId())) {
            throw new IllegalStateException("Creator cannot join as opponent.");
        }
        game.setOpponent(opponentPlayer);
        game.setUpdatedTimestamp(LocalDateTime.now());

        return gameDAO.save(game);
    }

    @Transactional
    public Game setPlayerReadyState(Long gameId, Long playerId, boolean isReady) {
        Game game = getGameOrThrow(gameId);
        Player player = getPlayerOrThrow(playerId);

        if (game.getStatus() != Game.GameStatus.WAITING) {
            throw new IllegalStateException("Game is not in WAITING status, cannot change ready state.");
        }

        if (game.getCreator().getPlayerId().equals(playerId)) {
            game.setCreatorReady(isReady);
        } else if (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(playerId)) {
            game.setOpponentReady(isReady);
        } else {
            throw new IllegalStateException("Player is not part of this game or cannot set ready state.");
        }
        
        game.setUpdatedTimestamp(LocalDateTime.now());
        return gameDAO.save(game);
    }
 
    @Transactional
    public GameDTO startGame(Long gameId) {
        Game game = getGameOrThrow(gameId);
        
        Player creator = game.getCreator();
        Player opponent = game.getOpponent();

        if (creator == null || opponent == null) {
            throw new IllegalStateException("Game must have a creator and an opponent to start.");
        }

        if (!game.isCreatorReady() || !game.isOpponentReady()) {
            throw new IllegalStateException("Both players must be ready to start the game.");
        }

        if (game.getStatus() != Game.GameStatus.WAITING) {
             throw new IllegalStateException("Game is not in a startable state. Current status: " + game.getStatus());
        }
 
        game.setCurrentRound(1);
        game.setCreatorRoundWins(0);
        game.setOpponentRoundWins(0);
        game.getRounds().clear();
        game.setWinner(null);
        game.setStatus(Game.GameStatus.IN_PROGRESS);

        try {
            startNewRound(game);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to initialize grid states for the first round", e);
        }
        
        Game savedGame = gameDAO.save(game);
        return new GameDTO(savedGame, null);
    }

    private void startNewRound(Game game) throws JsonProcessingException {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null for starting a new round.");
        }
        Player creator = game.getCreator();
        Player opponent = game.getOpponent();
        Grid gameGrid = game.getGrid();

        if (creator == null || opponent == null || gameGrid == null || gameGrid.getCharacters() == null || gameGrid.getCharacters().size() < 2) {
            throw new IllegalStateException("Cannot start new round: Game creator, opponent, or grid with sufficient characters is missing.");
        }

        Round newRound = new Round(game.getCurrentRound(), game);
        newRound.setStatus(dev.triomph.kies.pojo.Status.IN_PROGRESS);
        game.addRound(newRound);

        game.setCreatorGridState(initializeGridStateForPlayer(gameGrid));
        game.setOpponentGridState(initializeGridStateForPlayer(gameGrid));

        List<Character> availableCharacters = new ArrayList<>(gameGrid.getCharacters());
        if (availableCharacters.size() < 2) {
            throw new IllegalStateException("Not enough unique characters in the grid to assign for the new round.");
        }
        Random random = new Random();
        int creatorCharIndex = random.nextInt(availableCharacters.size());
        game.setCreatorSecretCharacterId(availableCharacters.remove(creatorCharIndex).getCharacterId());
        int opponentCharIndex = random.nextInt(availableCharacters.size());
        game.setOpponentSecretCharacterId(availableCharacters.remove(opponentCharIndex).getCharacterId());

        game.setCurrentTurnPlayerId(random.nextBoolean() ? creator.getPlayerId() : opponent.getPlayerId());
        
    }

    private String initializeGridStateForPlayer(Grid grid) throws JsonProcessingException {
        if (grid == null || grid.getCharacters() == null || grid.getCharacters().isEmpty()) {
            return "{}";
        }
        Map<Long, Boolean> initialGridState = new HashMap<>();
        for (Character character : grid.getCharacters()) {
            initialGridState.put(character.getCharacterId(), false);
        }
        return objectMapper.writeValueAsString(initialGridState);
    }
    
    @Transactional
    public Question askQuestion(Long gameId, Long askingPlayerId, String questionText) {
        Game game = getGameOrThrow(gameId);
        Player askingPlayer = getPlayerOrThrow(askingPlayerId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }
        if (!game.getCurrentTurnPlayerId().equals(askingPlayerId)) {
            throw new IllegalStateException("It's not this player's turn.");
        }

        Player targetPlayer;
        if (game.getCreator().getPlayerId().equals(askingPlayerId)) {
            targetPlayer = game.getOpponent();
        } else if (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(askingPlayerId)) {
            targetPlayer = game.getCreator();
        } else {
            throw new IllegalStateException("Asking player is not part of this game.");
        }
        if (targetPlayer == null) {
             throw new IllegalStateException("Target player (opponent) not found in the game.");
        }

        Integer currentRound = 1;

        Question question = new Question(game, askingPlayer, targetPlayer, questionText, currentRound);
        return questionDAO.save(question);
    }

    @Transactional
    public Question answerQuestion(Long gameId, Long answeringPlayerId, Long questionId, Answer answer) {
        Game game = getGameOrThrow(gameId);
        Player answeringPlayer = getPlayerOrThrow(answeringPlayerId);
        Question question = getQuestionOrThrow(questionId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }
        if (!question.getGame().getGameId().equals(gameId)) {
            throw new IllegalArgumentException("Question does not belong to this game.");
        }
        if (!question.getTargetPlayer().getPlayerId().equals(answeringPlayerId)) {
            throw new IllegalStateException("This player is not the target of the question.");
        }
        if (question.getAnswer() != Answer.PENDING) {
            throw new IllegalStateException("Question has already been answered.");
        }

        question.setAnswer(answer);
        Question savedQuestion = questionDAO.save(question);

        if (game.getCurrentTurnPlayerId().equals(question.getAskingPlayer().getPlayerId())) {
             Player nextTurnPlayer = question.getTargetPlayer().getPlayerId().equals(game.getCreator().getPlayerId()) ? game.getCreator() : game.getOpponent();
             if (nextTurnPlayer == null && question.getTargetPlayer().getPlayerId().equals(game.getOpponent().getPlayerId())) {
                 nextTurnPlayer = game.getCreator();
             } else if (nextTurnPlayer == null) {
                 throw new IllegalStateException("Could not determine next turn player.");
             }
             game.setCurrentTurnPlayerId(nextTurnPlayer.getPlayerId());
        } else {
            game.setCurrentTurnPlayerId(question.getAskingPlayer().getPlayerId());
        }
        gameDAO.save(game);
        return savedQuestion;
    }

    @Transactional
    public Game makeGuess(Long gameId, Long guessingPlayerId, Long guessedCharacterId) {
        Game game = getGameOrThrow(gameId);
        Player guessingPlayer = getPlayerOrThrow(guessingPlayerId);
        Character guessedCharacter = getCharacterOrThrow(guessedCharacterId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }

        boolean isCurrentTurn = game.getCurrentTurnPlayerId().equals(guessingPlayerId);
        boolean isAskerOfLastAnsweredQuestion = false;
        Optional<Question> lastAnsweredQuestionOpt = questionDAO.findTopByGameAndAnswerNotOrderByTimestampDesc(game, dev.triomph.kies.pojo.Answer.PENDING);

        if (lastAnsweredQuestionOpt.isPresent()) {
            Question lastAnsweredQuestion = lastAnsweredQuestionOpt.get();
            if (lastAnsweredQuestion.getAskingPlayer().getPlayerId().equals(guessingPlayerId) &&
                lastAnsweredQuestion.getTargetPlayer().getPlayerId().equals(game.getCurrentTurnPlayerId())) {
                isAskerOfLastAnsweredQuestion = true;
            }
        }
        if (!isCurrentTurn && !isAskerOfLastAnsweredQuestion) {
            throw new IllegalStateException("It's not this player's turn to make a guess, nor did they just receive an answer to their question.");
        }

        Round currentRound = game.getRounds().stream()
                                .filter(r -> r.getRoundNumber().equals(game.getCurrentRound()) && r.getStatus() == dev.triomph.kies.pojo.Status.IN_PROGRESS)
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Active round not found for game " + gameId + " and round number " + game.getCurrentRound()));
        
        Long opponentActualSecretCharacterId;
        Player opponentPlayer;

        if (game.getCreator().getPlayerId().equals(guessingPlayerId)) {
            opponentActualSecretCharacterId = game.getOpponentSecretCharacterId();
            opponentPlayer = game.getOpponent();
        } else if (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(guessingPlayerId)) {
            opponentActualSecretCharacterId = game.getCreatorSecretCharacterId();
            opponentPlayer = game.getCreator();
        } else {
            throw new IllegalStateException("Guessing player is not part of this game.");
        }

        if (opponentActualSecretCharacterId == null) {
            throw new IllegalStateException("Opponent's secret character is not set for the current round.");
        }
        if (opponentPlayer == null) {
            throw new IllegalStateException("Opponent player could not be determined for round result.");
        }

        boolean guessIsCorrect = opponentActualSecretCharacterId.equals(guessedCharacter.getCharacterId());

        if (guessIsCorrect) {
            currentRound.setWinner(guessingPlayer);
            if (game.getCreator().getPlayerId().equals(guessingPlayerId)) {
                game.setCreatorRoundWins(game.getCreatorRoundWins() + 1);
            } else {
                game.setOpponentRoundWins(game.getOpponentRoundWins() + 1);
            }
        } else {
            currentRound.setWinner(opponentPlayer);
            if (game.getCreator().getPlayerId().equals(opponentPlayer.getPlayerId())) {
                game.setCreatorRoundWins(game.getCreatorRoundWins() + 1);
            } else {
                game.setOpponentRoundWins(game.getOpponentRoundWins() + 1);
            }
        }
        currentRound.setStatus(dev.triomph.kies.pojo.Status.COMPLETED);

        if (game.getCurrentRound() >= game.getMaxRounds()) {
            Player winner = null;
            if (game.getCreatorRoundWins() > game.getOpponentRoundWins()) {
                winner = game.getCreator();
                game.setWinner(winner);
            } else if (game.getOpponentRoundWins() > game.getCreatorRoundWins()) {
                winner = game.getOpponent();
                game.setWinner(winner);
            } else {
                game.setWinner(null);
            }

            if (winner != null) {
                playerService.incrementVictories(winner.getPlayerId());
            }
            game.setStatus(Game.GameStatus.FINISHED);
        } else {
            game.setCurrentRound(game.getCurrentRound() + 1);
            try {
                startNewRound(game);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to start next round due to grid state processing error.", e);
            }
        }
        
        return gameDAO.save(game);
    }

    @Transactional
    public Game flipCard(Long gameId, Long playerId, Long characterId, boolean isFlippedDown) {
        Game game = getGameOrThrow(gameId);
        Player player = getPlayerOrThrow(playerId);
        Character characterToFlip = getCharacterOrThrow(characterId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }
        
        if (game.getGrid() == null || game.getGrid().getCharacters().stream().noneMatch(c -> c.getCharacterId().equals(characterId))) {
            throw new IllegalArgumentException("Character ID " + characterId + " is not part of the game's grid.");
        }

        String playerGridStateJson;
        boolean isCreator = game.getCreator().getPlayerId().equals(playerId);
        boolean isOpponent = game.getOpponent() != null && game.getOpponent().getPlayerId().equals(playerId);

        if (isCreator) {
            playerGridStateJson = game.getCreatorGridState();
        } else if (isOpponent) {
            playerGridStateJson = game.getOpponentGridState();
        } else {
            throw new IllegalStateException("Player is not part of this game or not active.");
        }

        try {
            Map<Long, Boolean> gridState = objectMapper.readValue(playerGridStateJson, new TypeReference<Map<Long, Boolean>>() {});
            if (!gridState.containsKey(characterId)) {
                 throw new IllegalArgumentException("Character ID " + characterId + " not found in player's grid state. This might indicate an uninitialized or corrupted grid state.");
            }
            gridState.put(characterId, isFlippedDown);
            String updatedGridStateJson = objectMapper.writeValueAsString(gridState);

            if (isCreator) {
                game.setCreatorGridState(updatedGridStateJson);
            } else {
                game.setOpponentGridState(updatedGridStateJson);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing grid state JSON", e);
        }
        
        return gameDAO.save(game);
    }
@Transactional
    public Game passTurn(Long gameId, Long playerId) {
        Game game = getGameOrThrow(gameId);
        Player player = getPlayerOrThrow(playerId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }
        if (!game.getCurrentTurnPlayerId().equals(playerId)) {
            throw new IllegalStateException("It's not this player's turn to pass.");
        }

        Player opponent;
        if (game.getCreator().getPlayerId().equals(playerId)) {
            opponent = game.getOpponent();
        } else if (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(playerId)) {
            opponent = game.getCreator();
        } else {
            throw new IllegalStateException("Player is not the creator or the opponent in this game.");
        }

        if (opponent == null) {
            throw new IllegalStateException("Cannot pass turn: Opponent not found.");
        }

        game.setCurrentTurnPlayerId(opponent.getPlayerId());
        game.setUpdatedTimestamp(LocalDateTime.now());
        return gameDAO.save(game);
    }

    public Game getGameState(Long gameId, Long playerId) {
        Game game = getGameOrThrow(gameId);
        Player player = getPlayerOrThrow(playerId);

        boolean isPlayerInGame = game.getCreator().getPlayerId().equals(playerId) ||
                                (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(playerId));
        boolean isSpectator = game.getSpectators().contains(playerId);

        if (!isPlayerInGame && !(game.getAllowSpectators() && isSpectator)) {
            throw new IllegalStateException("Player is not authorized to view this game state.");
        }
        
        return game;
    }
    
    @Transactional
    public Game addSpectator(Long gameId, Player spectatorPlayer) {
        Game game = getGameOrThrow(gameId);
        if (!game.getAllowSpectators()) {
            throw new IllegalStateException("Spectators are not allowed for this game.");
        }
        Long spectatorId = spectatorPlayer.getPlayerId();
        if (game.getCreator().getPlayerId().equals(spectatorId) || (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(spectatorId))) {
            return game;
        }
        if (!game.getSpectators().contains(spectatorId)) {
            game.addSpectator(spectatorId);
            return gameDAO.save(game);
        }
        return game;
    }
    
    @Transactional
    public boolean removeSpectator(Long gameId, Player spectatorPlayer) {
        Game game = getGameOrThrow(gameId);
        boolean removed = game.removeSpectator(spectatorPlayer.getPlayerId());
        if (removed) {
            gameDAO.save(game);
        }
        return removed;
    }
    
    @Transactional
    public Game removeOpponent(Long gameId) {
        Game game = getGameOrThrow(gameId);
        if (game.getOpponent() == null) {
            return game;
        }
        game.setOpponent(null);
        game.setOpponentReady(false);
        game.setOpponentSecretCharacterId(null);

        if (game.getStatus() == Game.GameStatus.IN_PROGRESS) {
             game.setStatus(Game.GameStatus.WAITING);
        } else if (game.getStatus() == Game.GameStatus.WAITING) {

        }
        game.setUpdatedTimestamp(LocalDateTime.now());
        return gameDAO.save(game);
    }

    public Game updateGame(Game game) {
        game.setUpdatedTimestamp(LocalDateTime.now());
        return gameDAO.save(game);
    }


    public void deleteGame(Long gameId) {
        Game game = getGameOrThrow(gameId);
        questionDAO.deleteAll(questionDAO.findByGame(game));
        gameDAO.deleteById(gameId);
    }
}