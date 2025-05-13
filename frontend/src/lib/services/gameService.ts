import { writable, type Writable, get } from 'svelte/store';
import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { LogEntry } from '$lib/components/GameHistoryLog.svelte';
import { authService, authStore } from '$lib/services/authService';
import type { DisplayMessage } from '$lib/types';
import { goto } from '$app/navigation';

export interface Character {
  id: string;
  characterName: string;
  imageUrl: string;
  isFlipped: boolean;
  selectable: boolean;
  secret: boolean;
}

export interface PendingQuestion {
  id: string;
  text: string;
  askerId: string;
}

export interface GameState {
  gameId: string | null;
  status: 'WAITING' | 'IN_PROGRESS' | 'PLAYER_ONE_WON' | 'PLAYER_TWO_WON' | 'DRAW' | 'CANCELLED' | null;
  currentTurnPlayerId: string | null;
  currentTurnPlayerNickname?: string | null;
  playerOneId: string | null;
  playerOneNickname?: string | null;
  playerTwoId: string | null;
  playerTwoNickname?: string | null;
  playerOneGrid: Character[];
  playerTwoGrid: Character[];
  playerOneSecretCharacterId: string | null;
  playerTwoSecretCharacterId: string | null;
  pendingQuestion?: PendingQuestion | null;
  activeQuestionId?: string | null;
  creatorReady?: boolean;
  opponentReady?: boolean;

  maxRounds?: number;
  currentRound?: number;
  creatorRoundWins?: number;
  opponentRoundWins?: number;
  
  rawCreatorGridState?: Record<string, boolean> | null;
  rawOpponentGridState?: Record<string, boolean> | null;
  rawGridInfo?: BackendGridDTO | null;

  creator?: PlayerDetails | null;
  opponent?: PlayerDetails | null;
}

interface BackendGridDTO {
  gridId: number;
  name: string;
  characters: BackendCharacterDTO[];
}

interface BackendCharacterDTO {
  characterId: number | string;
  name: string;
  imageUrl: string;
}


function mapGameDTOtoGameState(dto: any): GameState {
  const creatorId = dto.creator?.playerId?.toString() || null;
  const opponentId = dto.opponent?.playerId?.toString() || null;
  const localPlayerId = get(authStore).id?.toString() || null;

  const baseCharacters: BackendCharacterDTO[] = dto.gridInfo?.characters || [];
  
  const creatorFlippedMap: Record<string, boolean> = (dto.creatorGridState && typeof dto.creatorGridState === 'object')
                                                      ? dto.creatorGridState
                                                      : {};
  const opponentFlippedMap: Record<string, boolean> = (dto.opponentGridState && typeof dto.opponentGridState === 'object')
                                                       ? dto.opponentGridState
                                                       : {};

  const mapToFrontendCharacter = (charDto: BackendCharacterDTO, flippedMap: Record<string, boolean>): Character => {
    const charIdStr = charDto.characterId.toString();
    return {
      id: charIdStr,
      characterName: charDto.name,
      imageUrl: charDto.imageUrl,
      isFlipped: flippedMap[charIdStr] === true,
      selectable: true,
      secret: false
    };
  };

  const playerOneGrid: Character[] = baseCharacters.map(charDto => mapToFrontendCharacter(charDto, creatorFlippedMap));
  const playerTwoGrid: Character[] = baseCharacters.map(charDto => mapToFrontendCharacter(charDto, opponentFlippedMap));
  
  if (baseCharacters.length > 0) {
    if(playerOneGrid.every(c => c.isFlipped === undefined)) console.warn("[gameService] playerOneGrid all isFlipped undefined. creatorFlippedMap:", creatorFlippedMap, "baseChars:", baseCharacters);
  }

  const creatorSecretIdStr = dto.creatorSecretCharacterId?.toString() || null;
  const opponentSecretIdStr = dto.opponentSecretCharacterId?.toString() || null;

  if (localPlayerId && creatorId === localPlayerId && creatorSecretIdStr) {
    const secretChar = playerOneGrid.find(c => c.id === creatorSecretIdStr);
    if (secretChar) {
      secretChar.secret = true;
    }
  } else if (localPlayerId && opponentId === localPlayerId && opponentSecretIdStr) {
    const secretChar = playerTwoGrid.find(c => c.id === opponentSecretIdStr);
    if (secretChar) {
      secretChar.secret = true;
    }
  }

  let frontendStatus: GameState['status'] = dto.status;

  if (dto.status === 'FINISHED') {
    if (dto.winner && dto.winner.playerId) {
      const winnerIdStr = dto.winner.playerId.toString();
      if (winnerIdStr === creatorId) {
        frontendStatus = 'PLAYER_ONE_WON';
      } else if (winnerIdStr === opponentId) {
        frontendStatus = 'PLAYER_TWO_WON';
      } else {
        console.warn("[gameService] mapGameDTOtoGameState: Winner ID in 'FINISHED' game does not match creator or opponent. DTO:", dto);
        frontendStatus = 'DRAW';
      }
    } else {
      frontendStatus = 'DRAW';
    }
  } else if (dto.status && !['WAITING', 'IN_PROGRESS', 'PLAYER_ONE_WON', 'PLAYER_TWO_WON', 'DRAW', 'CANCELLED'].includes(dto.status)) {
    console.warn(`[gameService] mapGameDTOtoGameState: Received unhandled status '${dto.status}' from backend. Mapping to CANCELLED as a fallback.`);
    frontendStatus = 'CANCELLED';
  }


  return {
    gameId: dto.gameId?.toString() || null,
    status: frontendStatus,
    currentTurnPlayerId: dto.currentTurnPlayerId?.toString() || null,
    currentTurnPlayerNickname: dto.currentTurnPlayerNickname || null,
    
    playerOneId: creatorId,
    playerOneNickname: dto.creator?.nickname || null,
    playerOneGrid: playerOneGrid, 
    
    playerTwoId: opponentId,
    playerTwoNickname: dto.opponent?.nickname || null,
    playerTwoGrid: playerTwoGrid, 

    playerOneSecretCharacterId: dto.playerOneSecretCharacterId?.toString() || dto.creatorSecretCharacterId?.toString() || null,
    playerTwoSecretCharacterId: dto.playerTwoSecretCharacterId?.toString() || dto.opponentSecretCharacterId?.toString() || null,
    
    pendingQuestion: dto.pendingQuestion || null,
    activeQuestionId: dto.activeQuestionId?.toString() || null,
    creatorReady: dto.creatorReady || false,
    opponentReady: dto.opponentReady || false,

    maxRounds: dto.maxRounds,
    currentRound: dto.currentRound,
    creatorRoundWins: dto.creatorRoundWins,
    opponentRoundWins: dto.opponentRoundWins,
   
    creator: dto.creator || null,
    opponent: dto.opponent || null,
    rawGridInfo: dto.gridInfo || null,
    rawCreatorGridState: dto.creatorGridState || null,
    rawOpponentGridState: dto.opponentGridState || null,
  };
}


export interface Grid {
  gridId: number;
  name: string;
  characters: { length: number }[];
}

export interface CreateGameData {
  gridId: number;
  creatorPlayerId: number;
  maxRounds: number;
  turnLimit: number;
  password?: string;
  allowSpectators: boolean;
}

export interface PlayerDetails {
  id: string | number;
  nickname: string;
  profileImageUrl?: string;
}

export interface GameSession {
  gameId: number;
  creator: PlayerDetails;
  opponent?: PlayerDetails;
  password?: string;
  allowSpectators: boolean;
  status: string;
  grid?: { name: string; };
  maxRounds?: number;
  turnLimit?: number;
  playerOneId?: string | null;
  playerOneNickname?: string | null;
  playerTwoId?: string | null;
  playerTwoNickname?: string | null;
  playerOneGrid?: Character[];
  playerTwoGrid?: Character[];
  playerOneSecretCharacterId?: string | null;
  playerTwoSecretCharacterId?: string | null;
  currentTurnPlayerId?: string | null;
  spectators?: any[];
  creatorReady?: boolean;
  opponentReady?: boolean;
}

export interface GameSummary {
  gameId: number;
  hasOpponent: boolean;
  status: string;
  spectatorCount: number;
  lastUpdated: string;
  creatorNickname?: string;
  isPasswordProtected?: boolean;
  allowSpectators?: boolean;
}

export interface JoinGameRequestData {
  gameId: number;
  password?: string;
  mode: 'play' | 'spectate';
}


export const gameId: Writable<string | null> = writable(null);
export const gameState: Writable<GameState | null> = writable(null);
export const playerCharacters: Writable<Character[]> = writable([]);
export const opponentCharactersCount: Writable<number> = writable(0);
export const chatWindowMessages: Writable<DisplayMessage[]> = writable([]);
export const gameLog: Writable<LogEntry[]> = writable([]);
export const gameStatusMessage: Writable<string | null> = writable('Connecting to game...');
export const isConnected: Writable<boolean> = writable(false);
export const currentTurnPlayerId: Writable<string | null> = writable(null);

let stompClient: Client | null = null;
let gameSubscription: StompSubscription | null = null;
let errorSubscription: StompSubscription | null = null;
let chatSubscription: StompSubscription | null = null;

const VITE_PUBLIC_API_URL = import.meta.env.VITE_PUBLIC_API_URL || 'http://localhost:8080';
const WS_URL = `${VITE_PUBLIC_API_URL.replace(/^http/, 'ws')}/ws`;
console.log('[gameService] WebSocket URL:', WS_URL);

function getJwtToken(): string | null {
  return authService.getRawJwtToken();
}

export function connectWebSocket(gId: string) {
  if (stompClient && stompClient.active) {
    console.log('WebSocket already connected.');
    return;
  }

  gameId.set(gId);

  stompClient = new Client({
    webSocketFactory: () => {
      const sockJsUrl = WS_URL.replace(/^ws/, 'http');
      console.log('[gameService] SockJS URL:', sockJsUrl);
      return new SockJS(sockJsUrl);
    },
    connectHeaders: {
      Authorization: `Bearer ${getJwtToken()}`,
    },
    debug: (str) => {
      console.log('STOMP: ' + str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = async (frame) => {
    console.log('[gameService] stompClient.onConnect fired. Frame:', frame);
    isConnected.set(true);
    addLogEntry({ type: 'event', message: 'Connecté au serveur de jeu.', eventClass: 'system' });
    console.log('Connected to WebSocket server:', frame);

    gameSubscription = stompClient!.subscribe(`/topic/game/${gId}`, (message: IMessage) => {
      console.log('[gameService] Received STOMP message on /topic/game/', gId, message.body);
      handleGameUpdate(JSON.parse(message.body));
    });

    errorSubscription = stompClient!.subscribe('/user/queue/errors', (message: IMessage) => {
      console.error('[gameService] Received STOMP error message on /user/queue/errors', message.body);
      handleErrorMessage(JSON.parse(message.body));
    });
    
    chatSubscription = stompClient!.subscribe(`/topic/chat/${gId}`, (message: IMessage) => {
        console.log('[gameService] Received STOMP message on /topic/chat/', gId, message.body);
        const backendChatMsg = JSON.parse(message.body);
        
        const currentUser = authService.getUserInfo();
        const currentUserId = currentUser?.id?.toString();

        let displayMsgForChatWindow: DisplayMessage | null = null;
        
        let messageTimestamp: Date;
        try {
            messageTimestamp = backendChatMsg.timestamp ? new Date(backendChatMsg.timestamp) : new Date();
        } catch (e) {
            console.warn("[gameService] Failed to parse timestamp from backend message, using current time:", backendChatMsg.timestamp, e);
            messageTimestamp = new Date();
        }

        const messageId = messageTimestamp.getTime() + Math.random().toString(36).substring(2);

        if (backendChatMsg.type === 'MESSAGE' && backendChatMsg.senderNickname && backendChatMsg.content) {
            displayMsgForChatWindow = {
                id: messageId,
                timestamp: messageTimestamp,
                type: 'chat',
                username: backendChatMsg.senderNickname,
                avatarSrc: "https://placehold.co/88x88", 
                text: backendChatMsg.content,
                isCurrentUser: !!(backendChatMsg.senderId && currentUserId && backendChatMsg.senderId.toString() === currentUserId)
            };
            
            addLogEntry({
                type: 'event',
                message: `${backendChatMsg.senderNickname}: ${backendChatMsg.content}`,
                eventClass: 'system' 
            });

        } else if (backendChatMsg.type === 'JOIN' || backendChatMsg.type === 'LEAVE') {
            let eventText = backendChatMsg.content;
            if (!eventText) {
                const action = backendChatMsg.type === 'JOIN' ? 'joined' : 'left';
                eventText = action === 'joined' ? `Le joueur ${backendChatMsg.senderNickname || 'Inconnu'} a rejoint la partie.` : `Le joueur ${backendChatMsg.senderNickname || 'Inconnu'} a quitté la partie.`;
            }
            displayMsgForChatWindow = {
                id: messageId,
                timestamp: messageTimestamp,
                type: 'event',
                text: eventText,
                eventClass: 'system'
            };
            addLogEntry({ type: 'event', message: eventText, eventClass: 'system' });

        } else if (backendChatMsg.type === 'QUESTION_COMMAND_ERROR') {
            displayMsgForChatWindow = {
                id: messageId,
                timestamp: messageTimestamp,
                type: 'event',
                text: backendChatMsg.content,
                eventClass: 'error'
            };
            addLogEntry({ type: 'event', message: backendChatMsg.content, eventClass: 'error' });
        
        } else if (backendChatMsg.type) { 
            let systemMessageText = backendChatMsg.content;
            type EventClassType = Extract<DisplayMessage, { type: 'event' }>['eventClass'];
            let eventClassForDisplay: EventClassType = 'system'; 

            if (systemMessageText === null || systemMessageText === undefined || systemMessageText === "") {
                systemMessageText = `Événement: ${backendChatMsg.type.replace(/_/g, ' ').toLowerCase()}`;
                if (systemMessageText === "") { 
                    systemMessageText = "Événement système (pas de détails)";
                }
            }
            
            if (backendChatMsg.type.toUpperCase().includes('ERROR')) {
                eventClassForDisplay = 'error';
            }

            displayMsgForChatWindow = {
                    id: messageId,
                timestamp: messageTimestamp,
                type: 'event',
                text: systemMessageText,
                eventClass: eventClassForDisplay
            };

            addLogEntry({ type: 'event', message: `[${backendChatMsg.type}] ${systemMessageText}`, eventClass: eventClassForDisplay });
            
        } else {
            console.warn('[gameService] Received chat message with unexpected structure or null type:', backendChatMsg);
            displayMsgForChatWindow = {
                id: messageId,
                timestamp: messageTimestamp,
                type: 'event',
                text: `Système: Message non formaté reçu.`,
                eventClass: 'error'
            };
            addLogEntry({
                type: 'event',
                message: `Système: Message non formaté reçu. Brut: ${JSON.stringify(backendChatMsg)}`,
                eventClass: 'error'
            });
        }

        if (displayMsgForChatWindow) {
            chatWindowMessages.update(msgs => [...msgs, displayMsgForChatWindow!]);
        }
    });


    console.log('[gameService] Attempting to fetch initial game state for gameId:', gId);
    try {
      const initialState = await fetchGameState(gId); 
      console.log('[gameService] Fetched initial game state result:', initialState);

      if (initialState && typeof initialState === 'object' && 'gameId' in initialState) { 
        console.log('[gameService] Mapping and setting game state from HTTP fetch.');
        const mappedState = mapGameDTOtoGameState(initialState);
        gameState.set(mappedState);
        currentTurnPlayerId.set(mappedState.currentTurnPlayerId);
      } else if (initialState === null) {
        console.warn('[gameService] fetchGameState returned null. Game data might arrive via WebSocket.');
      } else if (initialState) {
         console.warn('[gameService] Fetched initial state received, but does not look like valid GameState:', initialState);
         addLogEntry({ type: 'event', message: 'Données initiales de jeu invalides.', eventClass: 'error' });
      }
    } catch (error) {
      console.error('[gameService] Error in onConnect during/after fetching initial game state:', error);
      addLogEntry({ type: 'event', message: 'Échec du chargement des données initiales du jeu.', eventClass: 'error' });
    }
  };

  stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
    addLogEntry({ type: 'event', message: `Erreur de connexion: ${frame.headers['message'] || 'Erreur STOMP inconnue'}`, eventClass: 'error' });
    isConnected.set(false);
  };

  stompClient.onDisconnect = () => {
    isConnected.set(false);
    addLogEntry({ type: 'event', message: 'Déconnecté du serveur de jeu.', eventClass: 'system' });
    console.log('Disconnected from WebSocket server.');
  };

  console.log('[gameService] Activating STOMP client...');
  stompClient.activate();
}

export function disconnectWebSocket() {
  if (stompClient) {
    gameSubscription?.unsubscribe();
    errorSubscription?.unsubscribe();
    chatSubscription?.unsubscribe();
    stompClient.deactivate();
    stompClient = null;
    isConnected.set(false);
    gameId.set(null);
    gameState.set(null);
    gameStatusMessage.set('Disconnected.');
  }
}

function handleGameUpdate(update: any) {
  console.log('[gameService] handleGameUpdate received:', update);

  if ( update.type === 'GAME_STATE' || 
       update.type === 'PLAYER_READY_UPDATE' || 
      (update.type === 'COUNTDOWN_CANCELLED' && update.updatedGameState) ||
      (update.type === 'PLAYER_JOINED' && update.data) || 
      (update.type === 'PLAYER_JOINED_AS_OPPONENT' && update.data) || 
      (update.type === 'PLAYER_JOINED_AS_SPECTATOR' && update.data) || 
      (update.type === 'PLAYER_LEFT' && update.data) || 
      (update.type === 'SPECTATOR_LEFT' && update.data)
  ) {
    const statePayload = update.payload || update.data || update.updatedGameState; 
    console.log(`[gameService] Processing state update (type: ${update.type}):`, statePayload);

    if (statePayload && typeof statePayload === 'object' && 'gameId' in statePayload) {
      const previousState = get(gameState);
      const mappedState = mapGameDTOtoGameState(statePayload);
      gameState.set(mappedState);
      currentTurnPlayerId.set(mappedState.currentTurnPlayerId);
 
      if (mappedState.status === 'IN_PROGRESS' && mappedState.currentTurnPlayerId &&
          (!previousState || mappedState.currentTurnPlayerId !== previousState.currentTurnPlayerId))
      {
        const playerName = mappedState.currentTurnPlayerNickname || `Joueur ${mappedState.currentTurnPlayerId}`;
        const turnMsg = `C'est au tour de ${playerName}.`;
        addLogEntry({ type: 'event', message: turnMsg, eventClass: 'system' });
        chatWindowMessages.update(msgs => [...msgs, {
          id: `turn_${Date.now()}_${mappedState.currentTurnPlayerId}`,
          timestamp: new Date(),
          type: 'event',
          text: turnMsg,
          eventClass: 'system'
        }]);
      }

      if (update.type.includes('JOINED') || update.type.includes('LEFT')) {
        let eventPlayerName = "A player";
        const eventPlayerId = update.playerId || (update.data ? (update.data.playerId || (update.data.creator && update.data.creator.playerId === update.playerId ? update.data.creator.nickname : null) || (update.data.opponent && update.data.opponent.playerId === update.playerId ? update.data.opponent.nickname : null) ) : null);

        if (eventPlayerId) {
            if (eventPlayerId === mappedState.playerOneId) eventPlayerName = mappedState.playerOneNickname || `Player ${eventPlayerId}`;
            else if (mappedState.playerTwoId && eventPlayerId === mappedState.playerTwoId) eventPlayerName = mappedState.playerTwoNickname || `Player ${eventPlayerId}`;
            else eventPlayerName = update.playerName || `Player ${eventPlayerId}`;
        } else if (update.type.includes('JOINED_AS_OPPONENT') && mappedState.playerTwoNickname) {
             eventPlayerName = mappedState.playerTwoNickname;
        } else if (update.playerName) {
            eventPlayerName = update.playerName;
        }


        let message = `${eventPlayerName} event occurred (${update.type})`;
        if (update.type.includes('JOINED')) message = `${eventPlayerName} joined.`;
        if (update.type.includes('LEFT')) message = `${eventPlayerName} left.`;
        addLogEntry({ type: 'event', message: message, eventClass: 'system' });
      }
      
      if (update.type === 'COUNTDOWN_CANCELLED') {
        console.log('[gameService] Countdown cancelled event processed, new state applied:', mappedState);
        document.dispatchEvent(new CustomEvent('gameCountdownCancelled', { detail: { gameId: mappedState.gameId } }));
      }

    } else {
      console.warn(`[gameService] Received ${update.type} WebSocket update with invalid/missing state payload:`, statePayload);
    }
  
  } else if (update.type === 'COUNTDOWN_INITIATED') {
    console.log('[gameService] Countdown initiated:', update);
    addLogEntry({ type: 'event', message: `Game starting in ${update.duration} seconds...`, eventClass: 'system' });
    document.dispatchEvent(new CustomEvent('gameCountdownInitiated', { detail: update }));

  } else if (update.type === 'GAME_STARTED') {
    console.log('[gameService] Game started:', update.data);
    if (update.data && typeof update.data === 'object' && 'gameId' in update.data) {
        const mappedState = mapGameDTOtoGameState(update.data);
        gameState.set(mappedState);
        currentTurnPlayerId.set(mappedState.currentTurnPlayerId);
        addLogEntry({ type: 'event', message: 'Game started!', eventClass: 'system' });
        
        if (mappedState.status === 'IN_PROGRESS' && mappedState.currentTurnPlayerId) {
          const playerName = mappedState.currentTurnPlayerNickname || `Joueur ${mappedState.currentTurnPlayerId}`;
          const turnMsg = `C'est au tour de ${playerName}.`;
          addLogEntry({ type: 'event', message: turnMsg, eventClass: 'system' });
          chatWindowMessages.update(msgs => [...msgs, {
            id: `turn_${Date.now()}_${mappedState.currentTurnPlayerId}`,
            timestamp: new Date(),
            type: 'event',
            text: turnMsg,
            eventClass: 'system'
          }]);
        }

        goto(`/game/${mappedState.gameId}`);
    } else {
        console.warn('[gameService] Received GAME_STARTED event with invalid payload:', update.data);
    }

  } else if (update.type === 'QUESTION_ASKED' || update.type === 'QUESTION_ASKED_VIA_CHAT') {
    const questionData = update.data;
    let qId: string | undefined,
        qText: string | undefined,
        askerId: string | undefined,
        askerNickname: string | undefined | null,
        targetId: string | undefined | null,
        qTimestamp: string | number | Date | undefined | null;

    if (questionData) {
      qId = questionData.questionId?.toString();
      qText = questionData.questionText;
      qTimestamp = questionData.timestamp;

      if (update.type === 'QUESTION_ASKED_VIA_CHAT') {
        askerId = questionData.askingPlayer?.playerId?.toString();
        askerNickname = questionData.askingPlayer?.nickname;
        targetId = questionData.targetPlayer?.playerId?.toString();
      } else {
        askerId = questionData.askingPlayerId?.toString();
        askerNickname = questionData.askingPlayerNickname;
        targetId = questionData.targetPlayerId?.toString();
      }
    }

    if (qId && qText && askerId) {
      gameState.update(gs => {
        if (!gs) return null;
        return {
          ...gs,
          pendingQuestion: {
            id: qId,
            text: qText,
            askerId: askerId,
          },
          activeQuestionId: qId,
        };
      });
      addLogEntry({
        type: 'event',
        message: `${askerNickname || 'Player ' + askerId} asked: ${qText}`,
        eventClass: 'system'
      });
      chatWindowMessages.update(msgs => [
        ...msgs,
        {
          id: qId + '_chat_event',
          timestamp: new Date(qTimestamp || Date.now()),
          type: 'event',
          text: qText,
          eventClass: 'question',
          username: askerNickname || `Player ${askerId}`,
          avatarSrc: "https://placehold.co/88x88",
          isCurrentUser: authService.getUserInfo().id?.toString() === askerId,
          questionDetails: {
            questionId: qId,
            originalQuestionText: qText,
            askingPlayerId: askerId,
            askingPlayerNickname: askerNickname ?? undefined,
            targetPlayerId: targetId ?? undefined,
            isAnswered: false,
          }
        }
      ]);
    } else {
      console.warn(`[gameService] Received ${update.type} event with incomplete data:`, questionData);
    }
  } else if (update.type === 'QUESTION_ANSWERED') { 
    const answeredQuestionData = update.data; 
    if (answeredQuestionData && answeredQuestionData.questionId) {
      const questionIdStr = answeredQuestionData.questionId.toString();
      
      chatWindowMessages.update(msgs =>
        msgs.map(msg => {
          if (msg.type === 'event' && msg.eventClass === 'question' && msg.questionDetails?.questionId === questionIdStr) {
            return {
              ...msg,
              questionDetails: {
                ...(msg.questionDetails!), 
                isAnswered: true,
                answerGiven: answeredQuestionData.answer, 
              },
            };
          }
          return msg;
        })
      );

      addLogEntry({
        type: 'event',
        message: `${answeredQuestionData.answeringPlayerNickname || 'Le joueur ' + answeredQuestionData.answeringPlayerId} a répondu: ${answeredQuestionData.answer}. (À: ${answeredQuestionData.questionText})`,
        eventClass: 'answer'
      });

      const answerMsg = `${(answeredQuestionData.targetPlayer?.nickname) || 'Le joueur ' + (answeredQuestionData.targetPlayer?.playerId)} a répondu: ${answeredQuestionData.answer}.`;
      chatWindowMessages.update(msgs => [...msgs, {
        id: `answer_${questionIdStr}_${Date.now()}`,
        timestamp: new Date(answeredQuestionData.timestamp || Date.now()),
        type: 'event',
        text: answerMsg,
        eventClass: 'system'
      }]);

      gameState.update(gs => {
        if (gs && gs.activeQuestionId === questionIdStr) {
          return {
            ...gs,
            pendingQuestion: null,
            activeQuestionId: null,
          };
        }
        return gs;
      });
    } else {
      console.warn('[gameService] Received QUESTION_ANSWERED event with incomplete data:', answeredQuestionData);
    }
  } else if (update.type === 'ANSWER_PROVIDED') { 
     addLogEntry({ type: 'answer', playerId: update.playerId, playerName: update.playerName, questionText: update.questionText, answer: update.answer });
  } else if (update.type === 'GUESS_RESULT') {
    console.log('[gameService] Received GUESS_RESULT:', update.data);
    const guessData = update.data;
    if (guessData) {
      const guesserName = guessData.guessingPlayerNickname || `Player ${guessData.guessingPlayerId}`;
      const resultText = guessData.correct ? 'Correct!' : 'Incorrect.';
      const guessMsg = `${guesserName} guessed ${guessData.guessedCharacterName || 'character ' + guessData.guessedCharacterId}... ${resultText}`;

      chatWindowMessages.update(msgs => [...msgs, {
        id: `guess_result_${Date.now()}`,
        timestamp: new Date(),
        type: 'event',
        text: guessMsg,
        eventClass: guessData.correct ? 'system' : 'error'
      }]);

      if (guessData.updatedGameState) {
          const mappedState = mapGameDTOtoGameState(guessData.updatedGameState);
          gameState.set(mappedState);
          currentTurnPlayerId.set(mappedState.currentTurnPlayerId);
          
          let endMsgText = "";
          if (mappedState.status === 'PLAYER_ONE_WON') {
            endMsgText = mappedState.playerOneNickname ? `Game Over! ${mappedState.playerOneNickname} is the winner!` : "Game Over! Player 1 is the winner!";
          } else if (mappedState.status === 'PLAYER_TWO_WON') {
            endMsgText = mappedState.playerTwoNickname ? `Game Over! ${mappedState.playerTwoNickname} is the winner!` : "Game Over! Player 2 is the winner!";
          } else if (mappedState.status === 'DRAW') {
            endMsgText = "Game Over! It's a draw!";
          }

          if (endMsgText) {
              chatWindowMessages.update(msgs => [...msgs, {
                id: `game_flow_${Date.now()}`,
                timestamp: new Date(),
                type: 'event',
                text: endMsgText,
                eventClass: 'system'
              }]);
              gameStatusMessage.set(endMsgText);
          }
      } else {
          console.warn('[gameService] GUESS_RESULT received without updatedGameState. Full state update expected.');
      }
    }
  } else if (update.type === 'GUESS_MADE') {
    console.log('[gameService] Guess made (processing GUESS_MADE event):', update);
    const guesserId = update.guessingPlayerId?.toString();
    const guessedCharId = update.guessedCharacterId?.toString();
    const gameData = update.data;

    let roundGuessWasCorrect = false;

    if (gameData && typeof gameData === 'object' && 'gameId' in gameData) {
        const mappedState = mapGameDTOtoGameState(gameData);
        const previousState = get(gameState);
        
        gameState.set(mappedState);
        currentTurnPlayerId.set(mappedState.currentTurnPlayerId);

        if (typeof update.isCorrect === 'boolean') {
            roundGuessWasCorrect = update.isCorrect;
        } else {
            console.warn("[gameService] GUESS_MADE 'isCorrect' field missing or not a boolean in update:", update);
        }

        const guesserNickname = mappedState.playerOneId === guesserId ? mappedState.playerOneNickname : mappedState.playerTwoNickname;
        const guessedChar = mappedState.playerOneGrid.find(c => c.id === guessedCharId) || mappedState.playerTwoGrid.find(c => c.id === guessedCharId);
        const guessedCharName = guessedChar?.characterName || `Character ${guessedCharId}`;
        const resultText = roundGuessWasCorrect ? "correctly" : "incorrectly";
        const guessMessage = `${guesserNickname || 'Joueur ' + guesserId} a deviné ${guessedCharName} ${resultText === 'correctly' ? 'correctement' : 'incorrectement'}.`;
        
        addLogEntry({ type: 'event', message: guessMessage, eventClass: roundGuessWasCorrect ? 'system' : 'error' });
        chatWindowMessages.update(msgs => [...msgs, {
            id: `guess_${Date.now()}_${guesserId}`,
            timestamp: new Date(),
            type: 'event',
            text: guessMessage,
            eventClass: roundGuessWasCorrect ? 'system' : 'error'
        }]);

        const isGameOver = mappedState.status === 'PLAYER_ONE_WON' || mappedState.status === 'PLAYER_TWO_WON' || mappedState.status === 'DRAW';
        if (isGameOver) {
            let winnerNickname = "No one (draw)";
            if (mappedState.status === 'PLAYER_ONE_WON') {
                winnerNickname = mappedState.playerOneNickname || 'Player 1';
            } else if (mappedState.status === 'PLAYER_TWO_WON') {
                 winnerNickname = mappedState.playerTwoNickname || 'Player 2';
            }
            
            const endMessage = `Partie terminée ! ${winnerNickname === 'No one (draw)' ? 'Égalité' : 'Gagnant : ' + winnerNickname}. Score final : ${mappedState.playerOneNickname} ${mappedState.creatorRoundWins} - ${mappedState.opponentRoundWins} ${mappedState.playerTwoNickname}`;
            addLogEntry({ type: 'event', message: endMessage, eventClass: 'system' });
            chatWindowMessages.update(msgs => [
                ...msgs,
                {
                    id: `gameover_${Date.now()}`,
                    timestamp: new Date(),
                    type: 'event',
                    text: endMessage,
                    eventClass: 'system'
                }
            ]);
            gameStatusMessage.set(endMessage);
        }
        else if (mappedState.status === 'IN_PROGRESS' && previousState && mappedState.currentRound && previousState.currentRound && mappedState.currentRound > previousState.currentRound) {
            const roundStartMessage = `Manche ${mappedState.currentRound} sur ${mappedState.maxRounds}. Score : ${mappedState.playerOneNickname} ${mappedState.creatorRoundWins} - ${mappedState.opponentRoundWins} ${mappedState.playerTwoNickname}.`;
            addLogEntry({ type: 'event', message: roundStartMessage, eventClass: 'system' });
             chatWindowMessages.update(msgs => [...msgs, {
                id: `round_start_${Date.now()}_${mappedState.currentRound}`,
                timestamp: new Date(),
                type: 'event',
                text: roundStartMessage,
                eventClass: 'system'
            }]);
            
            console.log('[gameService] New round started, fetching personalized game state...');
            fetchGameState(mappedState.gameId!).then(personalizedState => {
              if (personalizedState) {
                console.log('[gameService] Received personalized state after round change:', personalizedState);
                const remappedState = mapGameDTOtoGameState(personalizedState);
                gameState.set(remappedState);
                currentTurnPlayerId.set(remappedState.currentTurnPlayerId);
              } else {
                 console.warn('[gameService] Failed to fetch personalized state after round change.');
              }
            }).catch(err => {
                 console.error('[gameService] Error fetching personalized state after round change:', err);
            });

        }
        else if (mappedState.status === 'IN_PROGRESS' && previousState && mappedState.currentTurnPlayerId !== previousState.currentTurnPlayerId) {
             const playerName = mappedState.currentTurnPlayerNickname || `Joueur ${mappedState.currentTurnPlayerId}`;
             const turnMsg = `C'est au tour de ${playerName}.`;
             const lastMsg = get(chatWindowMessages).slice(-1)[0];
             if (!lastMsg || !lastMsg.text.includes(turnMsg)) {
                 addLogEntry({ type: 'event', message: turnMsg, eventClass: 'system' });
                 chatWindowMessages.update(msgs => [...msgs, {
                   id: `turn_change_${Date.now()}_${mappedState.currentTurnPlayerId}`,
                   timestamp: new Date(),
                   type: 'event',
                   text: turnMsg,
                   eventClass: 'system'
                 }]);
             }
        }

    } else {
        console.warn('[gameService] GUESS_MADE message did not contain valid game data in update.data.');
    }

  } else if (update.type === 'CARD_FLIPPED') {

  } else if (update.type === 'GAME_OVER') {
    console.log('[gameService] GAME_OVER received, data:', update.data);
    if (update.data && typeof update.data === 'object' && 'gameId' in update.data) {
      const mappedState = mapGameDTOtoGameState(update.data);
      gameState.set(mappedState);
      currentTurnPlayerId.set(mappedState.currentTurnPlayerId);

      let gameOverMessage = "Game Over!";
      if (mappedState.status === 'PLAYER_ONE_WON') {
          gameOverMessage = `${mappedState.playerOneNickname || mappedState.creator?.nickname || 'Player 1'} wins!`;
      } else if (mappedState.status === 'PLAYER_TWO_WON') {
          gameOverMessage = `${mappedState.playerTwoNickname || mappedState.opponent?.nickname || 'Player 2'} wins!`;
      } else if (mappedState.status === 'DRAW') {
          gameOverMessage = "It's a draw!";
      }
      addLogEntry({ type: 'event', message: gameOverMessage, eventClass: 'system' });
      gameStatusMessage.set(gameOverMessage);

    } else {
      console.warn('[gameService] Received GAME_OVER event with invalid/missing data payload:', update.data);
      addLogEntry({ type: 'event', message: 'Game Over: Received incomplete data.', eventClass: 'error' });
    }
  } else {
    console.warn('[gameService] Received unhandled WebSocket update type:', update.type, update);
  }
}

function handleErrorMessage(error: any) {
  console.error('Error message received:', error);
  const specificErrorMessage = error && error.error ? error.error : 'An unknown error occurred.';
  gameStatusMessage.set(`Error: ${specificErrorMessage}`);
  addLogEntry({ type: 'event', message: `Error: ${specificErrorMessage}`, eventClass: 'error' });
}

export function addLogEntry(
  entryData:
    | { type: 'question'; playerId: string; playerName: string; text: string; }
    | { type: 'answer'; playerId: string; playerName: string; questionText: string; answer: boolean; }
    | { type: 'guess'; playerId: string; playerName: string; characterName: string; correct: boolean; }
    | { type: 'event'; message: string; eventClass?: 'system' | 'question' | 'answer' | 'guess' | 'error'; }
) {
  const newEntry: LogEntry = {
    id: Date.now() + Math.random(), 
    timestamp: new Date(),          
    ...entryData
  };
  gameLog.update(entries => [...entries, newEntry]);
}


const baseApiUrl = VITE_PUBLIC_API_URL;

async function apiFetch(endpoint: string, options: RequestInit = {}) {
  const token = getJwtToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers,
  };

  const response = await fetch(`${baseApiUrl}${endpoint}`, { ...options, headers });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({ message: 'Request failed with status: ' + response.status }));
    throw new Error(errorData.message || `API request to ${endpoint} failed`);
  }
  return response.json();
}

export async function getAvailableGrids(): Promise<Grid[]> {
  try {
    const grids = await apiFetch('/api/grids'); 
    return grids as Grid[];
  } catch (error) {
    console.error("Failed to fetch available grids:", error);
    return []; 
  }
}

export async function createGameAPI(data: CreateGameData): Promise<GameState> {
  const createdGame: GameState = await apiFetch('/api/games', {
    method: 'POST',
    body: JSON.stringify(data),
  });
  return createdGame;
}

export async function joinGame(data: JoinGameRequestData): Promise<GameState> {
  const userInfo = authService.getUserInfo();
  if (!userInfo || userInfo.id === null) {
    throw new Error("Player ID not found. Cannot join game.");
  }
  const playerId = userInfo.id.toString();

  const joinedGame: GameState = await apiFetch(`/api/games/${data.gameId}/join`, {
    method: 'POST',
    body: JSON.stringify({
      playerId: playerId, 
      password: data.password,
      asSpectator: data.mode === 'spectate'
    }),
  });
  return joinedGame;
}


export async function setPlayerReadyState(gameId: string, isReady: boolean): Promise<void> {
  try {
    await apiFetch(`/api/games/${gameId}/ready`, {
      method: 'POST',
      body: JSON.stringify({ isReady }),
    });
    console.log(`[gameService] Player ready state set to ${isReady} for game ${gameId}. State update expected via WebSocket.`);
  } catch (error: any) {
    console.error(`[gameService] Failed to set player ready state for game ${gameId}:`, error);
    addLogEntry({ type: 'event', message: `Error setting ready state: ${error.message}`, eventClass: 'error' });
    throw error; 
  }
}
 
export async function fetchGameState(gId: string): Promise<GameState | null> {
  try {
    const state = await apiFetch(`/api/games/${gId}`);
    return state as GameState;
  } catch (error) {
    console.error(`[gameService] fetchGameState failed for game ${gId}:`, error);
    return null; 
  }
}

export async function getAllGames(): Promise<GameSession[]> {
  try {
    const games = await apiFetch('/api/games');
    return games as GameSession[]; 
  } catch (error) {
    console.error("Failed to fetch all games:", error);
    return []; 
  }
}

export async function getGamesSummary(): Promise<GameSummary[]> {
  try {
    const summaries = await apiFetch('/api/games/summary');
    return summaries as GameSummary[]; 
  } catch (error) {
    console.error("Failed to fetch game summaries:", error);
    return []; 
  }
}

export async function getGameById(id: number): Promise<GameSession | null> {
  try {
    const game = await apiFetch(`/api/games/${id}`);
    return game as GameSession; 
  } catch (error)
{
    console.error(`Failed to fetch game by ID ${id}:`, error);
    return null; 
  }
}

function sendWsMessage(destination: string, body: any) {
  if (stompClient && stompClient.active) {
    stompClient.publish({
      destination: destination,
      body: JSON.stringify(body),
    });
  } else {
    console.error('STOMP client not connected. Cannot send message.');
    gameStatusMessage.set('Not connected. Cannot send message.');
  }
}

export function sendChatMessage(gameId: string, text: string) {
    sendWsMessage(`/app/chat/${gameId}/send`, { content: text });
}

export async function askQuestion(gId: string, questionText: string): Promise<void> {
  const currentUser = authService.getUserInfo();
  if (!currentUser || !currentUser.id) {
    const errorMsg = 'User not authenticated or ID missing.';
    console.error('[gameService] askQuestion:', errorMsg);
    addLogEntry({ type: 'event', message: `Error: You must be logged in to ask a question.`, eventClass: 'error' });
    chatWindowMessages.update(msgs => [
      ...msgs,
      {
        id: Date.now().toString() + Math.random(),
        timestamp: new Date(),
        type: 'event',
        text: `Error: You must be logged in to ask a question.`,
        eventClass: 'error'
      }
    ]);
    return Promise.reject(new Error(errorMsg)); 
  }

  const chatMessageContent = `/question ${questionText}`;

  try {
    sendWsMessage(`/app/chat/${gId}/send`, { content: chatMessageContent });
    console.log('[gameService] askQuestion: Question submitted successfully via WebSocket chat command.');
  } catch (error: any) {
    console.error('[gameService] askQuestion: Failed to send question via WebSocket:', error.message || error);
    const errorText = `Error sending question: ${error.message || 'Connection issue?'}`;
    addLogEntry({ type: 'event', message: errorText, eventClass: 'error' });
    chatWindowMessages.update(msgs => [
      ...msgs,
      {
        id: Date.now().toString() + Math.random(),
        timestamp: new Date(),
        type: 'event',
        text: errorText,
        eventClass: 'error'
      }
    ]);
    throw error;
  }
}

export function answerQuestion(gId: string, questionId: string, answer: boolean) {
  const currentUser = authService.getUserInfo();
  if (!currentUser || !currentUser.id) {
    console.error('[gameService] answerQuestion: User not authenticated or ID missing.');
    addLogEntry({ type: 'event', message: `Error: You must be logged in to answer.`, eventClass: 'error' });
    return;
  }
  const answeringPlayerId = currentUser.id.toString();
  const answerString = answer ? "YES" : "NO";
  sendWsMessage(`/app/game/${gId}/answer`, { questionId, answer: answerString, answeringPlayerId });
}

export function makeGuess(gId: string, characterId: string) {
 const currentUser = authService.getUserInfo();
  if (!currentUser || !currentUser.id) {
    console.error('[gameService] makeGuess: User not authenticated or ID missing.');
    addLogEntry({ type: 'event', message: `Error: You must be logged in to guess.`, eventClass: 'error' });
    return;
  }
  const guessingPlayerId = currentUser.id.toString();
  const payload = { guessedCharacterId: characterId, guessingPlayerId };
  console.log('[gameService] makeGuess called. Sending guess for game:', gId, 'character:', characterId, 'Payload:', payload);
  sendWsMessage(`/app/game/${gId}/guess`, payload);
}

export function signalPassTurn(gId: string) {
  const currentUser = authService.getUserInfo();
  if (!currentUser || !currentUser.id) {
    console.error('[gameService] signalPassTurn: User not authenticated or ID missing.');
    addLogEntry({ type: 'event', message: `Error: You must be logged in to pass turn.`, eventClass: 'error' });
    return;
  }
  sendWsMessage(`/app/game/${gId}/pass`, {});
  console.log('[gameService] signalPassTurn: Pass turn signal sent.');
}

export function flipCard(gId: string, characterId: string, isFlippedDown: boolean) {
  sendWsMessage(`/app/game/${gId}/flip`, { characterId, isFlippedDown });
}

gameState.set(null);