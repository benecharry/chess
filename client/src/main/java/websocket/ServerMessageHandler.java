package websocket;

import chess.ChessGame;

public interface ServerMessageHandler {
    void handleLoadGame(ChessGame game);
    void handleError(String errorMessage);
    void handleNotification(String message);
}