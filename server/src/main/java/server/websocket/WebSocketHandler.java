package server.websocket;

import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataSQLDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataSQLDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import chess.ChessGame;
import chess.ChessMove;

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private GameDataSQLDataAccess gameDataSQLDataAccess = new GameDataSQLDataAccess();
    private AuthDataSQLDataAccess authDataSQLDataAccess = new AuthDataSQLDataAccess();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        //System.out.println("Session connected.");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        //System.out.println("Session closed.");
        sessions.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        //System.out.println("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String string) throws IOException {
        // Log the received message
        //System.out.println("Message received: " + string);
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(string, UserGameCommand.class);
            //System.out.println("Deserialized command: " + userGameCommand);

            switch (userGameCommand.getCommandType()) {
                case CONNECT:
                    connect(userGameCommand, session);
                    break;
                case MAKE_MOVE:
                    makeMove(userGameCommand, session);
                    break;
                case LEAVE:
                    leaveGame(userGameCommand, session);
                    break;
                case RESIGN:
                    resignGame(userGameCommand, session);
                    break;
                default:
                    sendError(new ErrorMessage("Unknown command type."), session);
            }
        } catch (DataAccessException e) {
            sendError(new ErrorMessage("An error occurred while accessing game data."), session);
            System.out.println("Data access exception: " + e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) {return;}

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) {return;}

        String joiningUser = authData.username();
        String whitePlayer = game.whiteUsername();
        String blackPlayer = game.blackUsername();
        String assignedRole = "";

        if (joiningUser.equals(whitePlayer)) {
            assignedRole = "white";
        } else if (joiningUser.equals(blackPlayer)) {
            assignedRole = "black";
        } else {
            if (whitePlayer == null) {
                assignedRole = "white";
                GameData updatedGame = new GameData(game.gameID(), joiningUser, blackPlayer, game.gameName(), game.game());
                gameDataSQLDataAccess.updateGame(updatedGame);
                game = updatedGame;
            } else if (blackPlayer == null) {
                assignedRole = "black";
                GameData updatedGame = new GameData(game.gameID(), whitePlayer, joiningUser, game.gameName(), game.game());
                gameDataSQLDataAccess.updateGame(updatedGame);
                game = updatedGame;
            } else {
                assignedRole = "observer";
            }
        }

        sessions.addSessionToGame(game.gameID(), session);

        ServerMessage loadGameMessage = new LoadGameMessage(game, assignedRole, true);
        sendMessage(loadGameMessage, session);

        String message = String.format("%s has joined the game as %s.", joiningUser, assignedRole);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) {return;}

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) {return;}

        ChessGame chessGame = game.game();

        if (chessGame.isGameOver()) {
            ErrorMessage errorMessage = new ErrorMessage("The game is already over.");
            sendError(errorMessage, session);
            return;
        }

        boolean isObserver = !authData.username().equals(game.whiteUsername()) &&
                !authData.username().equals(game.blackUsername());
        if (isObserver) {
            ErrorMessage errorMessage = new ErrorMessage("Observers cannot make moves.");
            sendError(errorMessage, session);
            return;
        }

        ChessMove move = command.getMove();
        ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != chessGame.getTeamTurn()) {
            ErrorMessage errorMessage = new ErrorMessage("Error: It's not your turn or you're trying to move your opponent's piece.");
            sendError(errorMessage, session);
            return;
        }

        try {
            chessGame.makeMove(move);

            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
            gameDataSQLDataAccess.updateGame(updatedGame);

            String pieceType = piece.getPieceType().name().toLowerCase();
            String playerColor = piece.getTeamColor().name().toLowerCase();

            ServerMessage loadGameMessage = new LoadGameMessage(updatedGame, playerColor, false);
            broadcastMessage(game.gameID(), loadGameMessage, null);

            String moveMessage = String.format("%s (%s) moved their %s from %s to %s.",
                    authData.username(), playerColor, pieceType,
                    move.getStartPosition().toString(), move.getEndPosition().toString());
            ServerMessage moveNotification = new NotificationMessage(moveMessage);
            broadcastMessage(game.gameID(), moveNotification, session);

        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid move: " + e.getMessage());
            sendError(errorMessage, session);
        }
    }


    private void leaveGame(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) {return;}

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) {return;}

        String leavingUser = authData.username();
        String role;

        if (leavingUser.equals(game.whiteUsername())) {
            role = "white";
            GameData updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            gameDataSQLDataAccess.updateGame(updatedGame);
            game = updatedGame;
        } else if (leavingUser.equals(game.blackUsername())) {
            role = "black";
            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            gameDataSQLDataAccess.updateGame(updatedGame);
            game = updatedGame;
        } else {
            role = "observer";
        }

        sessions.removeSessionFromGame(game.gameID(), session);

        String message = String.format("%s has left the game as %s.", leavingUser, role);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);

        session.close();
    }


    private void resignGame(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) {
            sendError(new ErrorMessage("Invalid authentication token."), session);
            return;
        }

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) {
            sendError(new ErrorMessage("Invalid game ID."), session);
            return;
        }

        ChessGame chessGame = game.game();
        if (chessGame.isGameOver()) {
            sendError(new ErrorMessage("The game is already over."), session);
            return;
        }

        String resigningUser = authData.username();

        if (!resigningUser.equals(game.whiteUsername()) && !resigningUser.equals(game.blackUsername())) {
            sendError(new ErrorMessage("Observers cannot resign the game."), session);
            return;
        }
        chessGame.setGameOver(true);

        GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        gameDataSQLDataAccess.updateGame(updatedGame);

        String resigningUserMessage = String.format("You have resigned. The game is over.");
        ServerMessage resigningUserNotification = new NotificationMessage(resigningUserMessage);
        sendMessage(resigningUserNotification, session);

        String message = String.format("%s has resigned. The game is over.", resigningUser);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);
        //Not so sure about this.
        session.close();
    }

    private AuthData validateAuthToken(String authToken, Session session) throws IOException, DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            sendError(new ErrorMessage("Invalid authentication token."), session);
            return null;
        }

        AuthData authData = authDataSQLDataAccess.getAuth(authToken);
        if (authData == null) {
            sendError(new ErrorMessage("Invalid authentication token."), session);
            return null;
        }
        return authData;
    }

    private GameData validateGameID(Integer gameID, Session session) throws IOException, DataAccessException {
        if (gameID == null || gameID <= 0) {
            sendError(new ErrorMessage("Invalid game ID."), session);
            return null;
        }
        GameData game = gameDataSQLDataAccess.getGame(gameID);
        if (game == null) {
            sendError(new ErrorMessage("Game not found."), session);
            return null;
        }
        return game;
    }

    private void sendMessage(ServerMessage message, Session session) throws IOException {
        String jsonMessage = new Gson().toJson(message);
        //System.out.println("Sending message: " + jsonMessage + " to session: " + session);
        session.getRemote().sendString(jsonMessage);
    }

    private void sendError(ErrorMessage errorMessage, Session session) throws IOException{
        sendMessage(errorMessage, session);
    }

    private void broadcastMessage(int gameID, ServerMessage message, Session excludeSession) throws IOException {
        String jsonMessage = new Gson().toJson(message);
        Set<Session> sessions = this.sessions.getSessionsForGame(gameID);

        if (sessions != null) {
            List<Session> removeList = new ArrayList<>();
            for (Session session : sessions) {
                if (session.isOpen()) {
                    if (!session.equals(excludeSession)) {
                        session.getRemote().sendString(jsonMessage);
                    }
                } else {
                    removeList.add(session);
                }
            }
            for (Session session : removeList) {
                this.sessions.removeSessionFromGame(gameID, session);
            }
        }
    }
}

