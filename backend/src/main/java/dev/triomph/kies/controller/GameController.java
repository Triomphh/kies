package dev.triomph.kies.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.dto.GameDTO;
import dev.triomph.kies.dto.JoinGameRequest;
import dev.triomph.kies.dto.CreateGameRequestDTO;
import dev.triomph.kies.dto.AnswerDTO;
import dev.triomph.kies.dto.GuessDTO;
import dev.triomph.kies.dto.PlayerGridUpdateDTO;
import dev.triomph.kies.dto.QuestionDTO;
import dev.triomph.kies.dto.PlayerReadyRequestDTO;
import dev.triomph.kies.dto.ChatMessageDTO;
import dev.triomph.kies.pojo.Round;
 
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import dev.triomph.kies.security.UserDetailsImpl;
import dev.triomph.kies.service.GameService;
import dev.triomph.kies.service.GridService;
import dev.triomph.kies.service.PlayerService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;
    private final PlayerService playerService;
    private final GridService gridService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, PlayerService playerService, GridService gridService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.gridService = gridService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames(@RequestParam(required = false) Long requestingPlayerId) {
        List<Game> games = gameService.getAllGames();
        List<GameDTO> gameDTOs = games.stream().map(game -> new GameDTO(game, true)).toList();
        return ResponseEntity.ok(gameDTOs);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<List<GameDTO>> getAllGamesSummary() {
        List<Game> games = gameService.getAllGames();
        List<GameDTO> gameSummaries = games.stream().map(game -> new GameDTO(game, true)).toList();
        return ResponseEntity.ok(gameSummaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id, @RequestParam(required = false) Long requestingPlayerId, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long effectivePlayerId = requestingPlayerId;
        if (currentUser != null && effectivePlayerId == null) {
            effectivePlayerId = currentUser.getId();
        }
        
        Optional<Game> gameOpt = gameService.getGameById(id);
        Long finalEffectivePlayerId = effectivePlayerId;
        return gameOpt
                .map(game -> ResponseEntity.ok(new GameDTO(game, finalEffectivePlayerId)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{gameId}/state/player/{playerId}")
    public ResponseEntity<GameDTO> getGameStateForPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        try {
            Game game = gameService.getGameState(gameId, playerId);
            return ResponseEntity.ok(new GameDTO(game, playerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getGameStatus(@PathVariable Long id) {
        Optional<Game> gameOpt = gameService.getGameById(id);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();
        Game game = gameOpt.get();
        Map<String, Object> gameStatus = new HashMap<>();
        gameStatus.put("gameId", game.getGameId());
        gameStatus.put("status", game.getStatus());
        gameStatus.put("currentTurnPlayerId", game.getCurrentTurnPlayerId());
        gameStatus.put("opponentId", game.getOpponent() != null ? game.getOpponent().getPlayerId() : null);
        gameStatus.put("spectatorCount", game.getSpectators() != null ? game.getSpectators().size() : 0);
        gameStatus.put("lastUpdated", game.getUpdatedTimestamp());
        return ResponseEntity.ok(gameStatus);
    }

    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody CreateGameRequestDTO createRequest, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        logger.info("Received createGame request: {}", createRequest);
        if (createRequest != null) {
            logger.info("Request DTO details: creatorPlayerId={}, gridId={}, maxRounds={}, turnLimit={}, passProvided={}, allowSpectators={}",
                createRequest.getCreatorPlayerId(), createRequest.getGridId(), createRequest.getMaxRounds(),
                createRequest.getTurnLimit(), createRequest.getPassword() != null && !createRequest.getPassword().isEmpty(),
                createRequest.getAllowSpectators());
        }

        try {
            Long creatorIdToUse = createRequest.getCreatorPlayerId();
            logger.info("Initial creatorPlayerId from DTO: {}", creatorIdToUse);

            if (currentUser != null) {
                logger.info("Authenticated currentUser: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            } else {
                logger.info("No authenticated currentUser (currentUser is null).");
            }

            if (creatorIdToUse == null && currentUser != null) {
                 creatorIdToUse = currentUser.getId();
                 logger.info("Using currentUser.getId() for creatorPlayerId: {}", creatorIdToUse);
            } else if (creatorIdToUse == null) {
                logger.warn("Creator Player ID is null in DTO and no authenticated user found. Returning BAD_REQUEST.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Creator Player ID is required and could not be determined.");
            }

            logger.info("Final creatorPlayerId to use for lookup: {}", creatorIdToUse);
            Optional<Player> creatorOpt = playerService.getPlayerById(creatorIdToUse);
            logger.info("Creator lookup result: present={}", creatorOpt.isPresent());

            Long gridIdFromRequest = createRequest.getGridId();
            logger.info("GridIdFromRequest for lookup: {}", gridIdFromRequest);
            Optional<Grid> gridOpt = Optional.empty();
            if (gridIdFromRequest != null) {
                gridOpt = gridService.getGridById(gridIdFromRequest);
            }
            logger.info("Grid lookup result: present={}", gridOpt.isPresent());
            
            if (creatorOpt.isPresent() && gridOpt.isPresent()) {
                Game game = gameService.createGame(
                    creatorOpt.get(),
                    createRequest.getMaxRounds(),
                    createRequest.getTurnLimit(),
                    gridOpt.get(),
                    createRequest.getPassword(),
                    createRequest.getAllowSpectators() != null ? createRequest.getAllowSpectators() : true
                );
                GameDTO gameDTO = new GameDTO(game, creatorOpt.get().getPlayerId());
                messagingTemplate.convertAndSend("/topic/games/new", gameDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(gameDTO);
            } else {
                 if(creatorOpt.isEmpty()) {
                    logger.warn("Creator not found for ID: {}. Returning BAD_REQUEST.", creatorIdToUse);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Creator not found with ID: " + creatorIdToUse);
                 }
                 if(gridOpt.isEmpty()) {
                    logger.warn("Grid not found for ID: {}. Returning BAD_REQUEST.", gridIdFromRequest);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Grid not found with ID: " + gridIdFromRequest);
                 }
                 logger.error("Reached unexpected state where creatorOpt or gridOpt is empty but not caught by specific checks.");
                 return ResponseEntity.badRequest().body("Unknown error determining creator or grid.");
            }
        } catch (Exception e) {
            logger.error("Exception during game creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during game creation: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/ready")
    public ResponseEntity<?> setPlayerReady(@PathVariable Long gameId, @RequestBody PlayerReadyRequestDTO readyRequest, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be authenticated.");
        }
        Long playerId = currentUser.getId();

        try {
            Game updatedGame = gameService.setPlayerReadyState(gameId, playerId, readyRequest.isReady());
            GameDTO responseGameDTO = new GameDTO(updatedGame, playerId);

            if (updatedGame.isCreatorReady() && updatedGame.isOpponentReady() && updatedGame.getStatus() == Game.GameStatus.WAITING) {
                logger.info("Both players ready for game {}. Starting game immediately.", gameId);
                try {
                    GameDTO startedGameDTO = gameService.startGame(gameId);
                    messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "GAME_STARTED", "data", startedGameDTO));
                } catch (Exception e) {
                    logger.error("Error during immediate game start for game {}: {}", gameId, e.getMessage(), e);
                    messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "GAME_START_ERROR", "message", "Failed to start game: " + e.getMessage()));
                }
            } else {
                logger.info("Player {} ready state changed to {} for game {}. Sending update.", playerId, readyRequest.isReady(), gameId);
                messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "PLAYER_READY_UPDATE", "data", new GameDTO(updatedGame, null)));
            }

            return ResponseEntity.ok(responseGameDTO);

        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to set player ready state for game {}: {}", gameId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             logger.error("Unexpected error setting player ready state for game {}: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/{gameId}/flip-card")
    public ResponseEntity<?> flipCard(@PathVariable Long gameId, @RequestBody PlayerGridUpdateDTO flipRequest, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        try {
            if (currentUser == null || !currentUser.getId().equals(flipRequest.getPlayerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Player ID in request does not match authenticated user.");
            }
            Game game = gameService.flipCard(gameId, flipRequest.getPlayerId(), flipRequest.getCharacterId(), flipRequest.getIsFlippedDown());
            GameDTO gameDTOForPlayer = new GameDTO(game, flipRequest.getPlayerId());
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "CARD_FLIPPED");
            payload.put("playerId", flipRequest.getPlayerId());
            payload.put("characterId", flipRequest.getCharacterId());
            payload.put("isFlippedDown", flipRequest.getIsFlippedDown());
            payload.put("updatedGridForPlayer", flipRequest.getPlayerId().equals(game.getCreator().getPlayerId()) ? gameDTOForPlayer.getCreatorGridState() : gameDTOForPlayer.getOpponentGridState());

            messagingTemplate.convertAndSendToUser(playerService.getPlayerById(flipRequest.getPlayerId()).orElseThrow().getNickname(), "/queue/reply", payload);
            messagingTemplate.convertAndSend("/topic/game/" + gameId, payload);
            
            return ResponseEntity.ok(new GameDTO(game, flipRequest.getPlayerId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDetailsToUpdate, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Optional<Game> gameOpt = gameService.getGameById(id);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();
        Game game = gameOpt.get();

        if (currentUser == null || !currentUser.getId().equals(game.getCreator().getPlayerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the game creator can update general settings.");
        }
        
        if (gameDetailsToUpdate.getMaxRounds() != null) game.setMaxRounds(gameDetailsToUpdate.getMaxRounds());
        if (gameDetailsToUpdate.getTurnLimit() != null) game.setTurnLimit(gameDetailsToUpdate.getTurnLimit());
        if (gameDetailsToUpdate.getAllowSpectators() != null) game.setAllowSpectators(gameDetailsToUpdate.getAllowSpectators());
        
        Game updatedGame = gameService.updateGame(game);
        GameDTO responseDto = new GameDTO(updatedGame, currentUser.getId());
        GameDTO broadcastDto = new GameDTO(updatedGame, null);
        messagingTemplate.convertAndSend("/topic/game/" + id, Map.of("type", "GAME_SETTINGS_UPDATED", "data", broadcastDto));
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Optional<Game> gameOpt = gameService.getGameById(id);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();
        Game game = gameOpt.get();

        if (currentUser == null || !currentUser.getId().equals(game.getCreator().getPlayerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the game creator can delete the game.");
        }
        
        gameService.deleteGame(id);
        messagingTemplate.convertAndSend("/topic/game/" + id, Map.of("type", "GAME_DELETED"));
        messagingTemplate.convertAndSend("/topic/games/deleted", Map.of("gameId", id));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGame(@PathVariable Long id, @RequestBody JoinGameRequest request, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        try {
            Long playerIdToJoin = request.getPlayerId();
            if (currentUser != null && !currentUser.getId().equals(playerIdToJoin)) {
                 if(! (request.getAsSpectator() != null && request.getAsSpectator()) ) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Player ID in request does not match authenticated user for joining as player.");
                 }
            }
             if (currentUser == null && playerIdToJoin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player ID is required to join.");
            }
            if (playerIdToJoin == null) playerIdToJoin = currentUser.getId();


            Optional<Player> playerOpt = playerService.getPlayerById(playerIdToJoin);
            Optional<Game> gameOpt = gameService.getGameById(id);
            
            if (!playerOpt.isPresent() || !gameOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Game game = gameOpt.get();
            
            if (game.getPassword() != null && !game.getPassword().isEmpty()) {
                if (request.getPassword() == null || !game.getPassword().equals(request.getPassword())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
                }
            }
            
            Game updatedGame;
            String eventType;
            if (request.getAsSpectator() != null && request.getAsSpectator()) {
                updatedGame = gameService.addSpectator(id, playerOpt.get());
                eventType = "PLAYER_JOINED_AS_SPECTATOR";
            } else {
                updatedGame = gameService.joinGame(id, playerOpt.get());
                eventType = "PLAYER_JOINED_AS_OPPONENT";
            }
            
            GameDTO responseGameDTO = new GameDTO(updatedGame, playerIdToJoin);
            GameDTO broadcastGameDTO = new GameDTO(updatedGame, null);

            String nickname = playerOpt.map(Player::getNickname).orElse("Un joueur");
            String frenchContent;
            boolean isSpectator = request.getAsSpectator() != null && request.getAsSpectator();

            if (isSpectator) {
                frenchContent = nickname + " regarde la partie.";
            } else {
                frenchContent = "Le joueur " + nickname + " a rejoint la partie.";
            }

            ChatMessageDTO systemChatMessage = new ChatMessageDTO();
            systemChatMessage.setGameId(id);
            systemChatMessage.setType("JOIN");
            systemChatMessage.setSenderNickname("Système");
            systemChatMessage.setSenderId(null);
            systemChatMessage.setContent(frenchContent);

            messagingTemplate.convertAndSend("/topic/chat/" + id, systemChatMessage);
            messagingTemplate.convertAndSend("/topic/game/" + id, Map.of("type", eventType, "data", broadcastGameDTO, "playerId", playerIdToJoin));
            return ResponseEntity.ok(responseGameDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    
    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveGame(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        try {
            Long playerIdToLeave = null;
            if (payload != null && payload.containsKey("playerId")) {
                playerIdToLeave = Long.parseLong(payload.get("playerId").toString());
            } else if (currentUser != null) {
                playerIdToLeave = currentUser.getId();
            }

            if (playerIdToLeave == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player ID is required to leave.");
            }

            Optional<Player> playerOpt = playerService.getPlayerById(playerIdToLeave);
            Optional<Game> gameOpt = gameService.getGameById(id);
            
            if (!playerOpt.isPresent() || !gameOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Game game = gameOpt.get();
            Player player = playerOpt.get();
            String eventType = "PLAYER_LEFT";
            GameDTO gameDTO = null;
            
            if (game.getCreator().getPlayerId().equals(playerIdToLeave)) {
                gameService.deleteGame(id);
                messagingTemplate.convertAndSend("/topic/game/" + id, Map.of("type", "GAME_DELETED_CREATOR_LEFT", "playerId", playerIdToLeave));
                messagingTemplate.convertAndSend("/topic/games/deleted", Map.of("gameId", id));
                return ResponseEntity.noContent().build();
            } else if (game.getOpponent() != null && game.getOpponent().getPlayerId().equals(playerIdToLeave)) {
                game = gameService.removeOpponent(id);
                gameDTO = new GameDTO(game, null);
            } else {
                boolean removed = gameService.removeSpectator(id, player);
                if (removed) {
                    game = gameService.getGameById(id).orElseThrow();
                    gameDTO = new GameDTO(game, null);
                    eventType = "SPECTATOR_LEFT";
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player not found in game as opponent or spectator.");
                }
            }

            messagingTemplate.convertAndSend("/topic/game/" + id, Map.of("type", eventType, "data", gameDTO, "playerId", playerIdToLeave));
            
            return ResponseEntity.ok(gameDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @MessageMapping("/game/{gameId}/answer")
    @Transactional
    public void handleAnswer(@DestinationVariable Long gameId, @Payload AnswerDTO answerRequest, SimpMessageHeaderAccessor headerAccessor) {
        UserDetailsImpl currentUser = getUserDetails(headerAccessor);
        if (currentUser == null) {
            sendErrorToUser(headerAccessor.getSessionId(), "Authentication required to answer.");
            return;
        }
        if (!currentUser.getId().equals(answerRequest.getAnsweringPlayerId())) {
             sendErrorToUser(headerAccessor.getSessionId(), "Player ID in answer request does not match authenticated user.");
            return;
        }

        try {
            Question question = gameService.answerQuestion(gameId, answerRequest.getAnsweringPlayerId(), answerRequest.getQuestionId(), answerRequest.getAnswer());
            QuestionDTO questionDTO = new QuestionDTO(question);
            
            Game updatedGame = gameService.getGameById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found after answering question: " + gameId)); 
            GameDTO gameDTO = new GameDTO(updatedGame, null);

            messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "QUESTION_ANSWERED", "data", questionDTO));
            messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "GAME_STATE_UPDATE", "data", gameDTO)); 
            logger.info("Processed answer for question {} in game {}", answerRequest.getQuestionId(), gameId);

        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Invalid answer request for game {}: {}", gameId, e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), "Invalid answer request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing answer for game {}: {}", gameId, e.getMessage(), e);
            sendErrorToUser(headerAccessor.getSessionId(), "Server error processing answer.");
        }
    }

    @Transactional
    @MessageMapping("/game/{gameId}/guess")
    public void handleGuess(@DestinationVariable Long gameId, @Payload GuessDTO guessRequest, SimpMessageHeaderAccessor headerAccessor) {
        UserDetailsImpl currentUser = getUserDetails(headerAccessor);
         if (currentUser == null) {
            sendErrorToUser(headerAccessor.getSessionId(), "Authentication required to make a guess.");
            return;
        }
        if (!currentUser.getId().equals(guessRequest.getGuessingPlayerId())) {
             sendErrorToUser(headerAccessor.getSessionId(), "Player ID in guess request does not match authenticated user.");
            return;
        }

        try {
            Game game = gameService.makeGuess(gameId, guessRequest.getGuessingPlayerId(), guessRequest.getGuessedCharacterId());
            GameDTO gameDTO = new GameDTO(game, null);
            
            Map<String, Object> messagePayload = new HashMap<>();
            messagePayload.put("type", "GUESS_MADE");
            messagePayload.put("data", gameDTO);
            messagePayload.put("guessingPlayerId", guessRequest.getGuessingPlayerId());
            messagePayload.put("guessedCharacterId", guessRequest.getGuessedCharacterId());
            
            boolean roundGuessWasCorrect = false;

            Integer concludedRoundNumber = (game.getStatus() == Game.GameStatus.FINISHED) ? game.getMaxRounds() : game.getCurrentRound() -1;
            if (concludedRoundNumber < 1 && game.getStatus() != Game.GameStatus.FINISHED) {
                concludedRoundNumber = 1;
            }


            Round concludedRound = null;
            if (game.getRounds() != null) {
                for (Round r : game.getRounds()) {
                    if (r.getRoundNumber().equals(concludedRoundNumber)) {
                        concludedRound = r;
                        break;
                    }
                }
            }

            if (concludedRound != null && concludedRound.getWinner() != null) {
                roundGuessWasCorrect = concludedRound.getWinner().getPlayerId().equals(guessRequest.getGuessingPlayerId());
            } else if (game.getStatus() == Game.GameStatus.FINISHED && game.getWinner() != null) {
                roundGuessWasCorrect = game.getWinner().getPlayerId().equals(guessRequest.getGuessingPlayerId());
            }


            messagePayload.put("isCorrect", roundGuessWasCorrect);
            messagePayload.put("roundWinnerId", concludedRound != null && concludedRound.getWinner() != null ? concludedRound.getWinner().getPlayerId() : null);
            messagePayload.put("concludedRoundNumber", concludedRoundNumber);


            messagingTemplate.convertAndSend("/topic/game/" + gameId, messagePayload);
            
            if (game.getStatus() == Game.GameStatus.FINISHED) {
                messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "GAME_OVER", "data", gameDTO));
                 logger.info("Game {} finished. Winner: {}. Scores: Creator {} - Opponent {}.",
                    gameId,
                    game.getWinner() != null ? game.getWinner().getNickname() : "Draw",
                    game.getCreatorRoundWins(),
                    game.getOpponentRoundWins());
            } else {
                 logger.info("Processed guess for character {} by player {} in game {}. Round {} ended. Next round: {}. Scores: Creator {} - Opponent {}.",
                    guessRequest.getGuessedCharacterId(),
                    guessRequest.getGuessingPlayerId(),
                    gameId,
                    concludedRoundNumber,
                    game.getCurrentRound(),
                    game.getCreatorRoundWins(),
                    game.getOpponentRoundWins());
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
             logger.warn("Invalid guess request for game {}: {}", gameId, e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), "Invalid guess request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing guess for game {}: {}", gameId, e.getMessage(), e);
            sendErrorToUser(headerAccessor.getSessionId(), "Server error processing guess.");
        }
    }
    
    @MessageMapping("/game/{gameId}/pass")
    public void handlePassTurn(@DestinationVariable Long gameId, SimpMessageHeaderAccessor headerAccessor) {
        UserDetailsImpl currentUser = getUserDetails(headerAccessor);
         if (currentUser == null) {
            sendErrorToUser(headerAccessor.getSessionId(), "Authentication required to pass turn.");
            return;
        }
        Long playerId = currentUser.getId();

        try {
            Game updatedGame = gameService.passTurn(gameId, playerId);
            GameDTO gameDTO = new GameDTO(updatedGame, null);

            messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "TURN_PASSED", "data", gameDTO));
            messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "GAME_STATE_UPDATE", "data", gameDTO));
            logger.info("Player {} passed turn in game {}", playerId, gameId);

        } catch (IllegalArgumentException | IllegalStateException e) {
             logger.warn("Invalid pass turn request for game {}: {}", gameId, e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), "Invalid pass turn request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing pass turn for game {}: {}", gameId, e.getMessage(), e);
            sendErrorToUser(headerAccessor.getSessionId(), "Server error processing pass turn.");
        }
    }

    private UserDetailsImpl getUserDetails(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getUser() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
            if (token.getPrincipal() instanceof UserDetailsImpl) {
                return (UserDetailsImpl) token.getPrincipal();
            }
        }
        logger.warn("Could not extract UserDetailsImpl from SimpMessageHeaderAccessor user: {}", headerAccessor.getUser());
        return null;
    }

    private void sendErrorToUser(String sessionId, String errorMessage) {
        if (sessionId != null) {
             messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", Map.of("error", errorMessage), createHeaders(sessionId));
        } else {
            logger.error("Cannot send error message '{}' because session ID is null.", errorMessage);
        }
       
    }
    
    private org.springframework.messaging.MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(org.springframework.messaging.simp.SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
    
    @Transactional
    @MessageMapping("/chat/{gameId}/send")
    public void handleChatMessage(@DestinationVariable Long gameId, @Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        UserDetailsImpl currentUser = null;
        java.security.Principal principal = headerAccessor.getUser();

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            Object principalObj = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (principalObj instanceof UserDetailsImpl) {
                currentUser = (UserDetailsImpl) principalObj;
            }
        }
        
        if (currentUser == null) {
            logger.error("Chat message received but currentUser could not be resolved from SimpMessageHeaderAccessor. Principal: {}", principal);
            return;
        }

        Long effectiveSenderId = currentUser.getId();
        if (chatMessage.getSenderId() != null && !chatMessage.getSenderId().equals(effectiveSenderId)) {
            logger.warn("Chat message senderId ({}) in payload does not match authenticated user ID ({}). Using authenticated user ID.", chatMessage.getSenderId(), effectiveSenderId);
        }
        chatMessage.setSenderId(effectiveSenderId);


        Player sender = playerService.getPlayerById(effectiveSenderId).orElse(null);
        if (sender != null) {
            chatMessage.setSenderNickname(sender.getNickname());
        } else {
             chatMessage.setSenderNickname("Unknown (" + chatMessage.getSenderId() + ")");
        }
        chatMessage.setTimestamp(java.time.LocalDateTime.now());
        chatMessage.setType("MESSAGE");


        if (chatMessage.getContent() != null && chatMessage.getContent().startsWith("/question ")) {
            try {
                String questionText = chatMessage.getContent().substring("/question ".length()).trim();
                if (!questionText.isEmpty()) {
                    Question question = gameService.askQuestion(gameId, chatMessage.getSenderId(), questionText);
                    QuestionDTO questionDTO = new QuestionDTO(question);
                    messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "QUESTION_ASKED_VIA_CHAT", "data", questionDTO));
                } else {

                }
            } catch (Exception e) {
                messagingTemplate.convertAndSendToUser(
                    currentUser.getUsername(),
                    "/queue/errors",
                    Map.of("error", "Failed to process /question command: " + e.getMessage())
                );
            }
        } else {
            messagingTemplate.convertAndSend("/topic/chat/" + gameId, chatMessage);
        }
    }
}