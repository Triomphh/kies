export interface QuestionDetailsForChat {
  questionId: string;
  originalQuestionText: string;
  askingPlayerId: string;
  askingPlayerNickname?: string;
  targetPlayerId?: string;
  isAnswered: boolean;
}

export type DisplayMessage = {
  id: string | number;
  timestamp: Date;
} & (
  | { type: 'chat'; username: string; avatarSrc: string; text: string; isCurrentUser: boolean }
  | { type: 'event'; text: string; eventClass?: 'system' | 'answer' | 'guess' | 'error' }
  | {
      type: 'event';
      eventClass: 'question';
      text: string;
      username?: string;
      avatarSrc?: string;
      isCurrentUser?: boolean;
      questionDetails: QuestionDetailsForChat;
    }
);