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
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private GameDataSQLDataAccess gameDataSQLDataAccess = new GameDataSQLDataAccess();
    private AuthDataSQLDataAccess authDataSQLDataAccess = new AuthDataSQLDataAccess();

    private final Map<Session, ChessGame.TeamColor> playerRoles = new ConcurrentHashMap<>();

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
        ChessGame.TeamColor assignedRole = null;

        if (joiningUser.equals(whitePlayer)) {
            assignedRole = ChessGame.TeamColor.WHITE;
        } else if (joiningUser.equals(blackPlayer)) {
            assignedRole = ChessGame.TeamColor.BLACK;
        } else {
            if (whitePlayer == null && blackPlayer == null) {
                assignedRole = ChessGame.TeamColor.OBSERVER;
            } else if (whitePlayer == null) {
                assignedRole = ChessGame.TeamColor.WHITE;
                GameData updatedGame = new GameData(game.gameID(), joiningUser, blackPlayer, game.gameName(), game.game());
                gameDataSQLDataAccess.updateGame(updatedGame);
                game = updatedGame;
            } else if (blackPlayer == null) {
                assignedRole = ChessGame.TeamColor.BLACK;
                GameData updatedGame = new GameData(game.gameID(), whitePlayer, joiningUser, game.gameName(), game.game());
                gameDataSQLDataAccess.updateGame(updatedGame);
                game = updatedGame;
            } else {
                assignedRole = ChessGame.TeamColor.OBSERVER;
            }
        }

        playerRoles.put(session, assignedRole);

        sessions.addSessionToGame(game.gameID(), session);

        String roleName = assignedRole.name().toLowerCase();

        ServerMessage loadGameMessage = new LoadGameMessage(game, roleName, true);
        sendMessage(loadGameMessage, session);

        String message = String.format("%s has joined the game as %s. " +
                "Please type <help> to see the game commands.", joiningUser, roleName);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);
    }


    private void makeMove(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) {return;}

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) {return;}

        ChessGame chessGame = game.game();
        ChessGame.TeamColor playerRole = playerRoles.get(session);

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

        if (piece == null || piece.getTeamColor() != playerRole) {
            ErrorMessage errorMessage = new ErrorMessage("Error: You're trying to move your opponent's piece.");
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

            String moveMessage = String.format("%s (%s) moved their %s from %s to %s. It's your turn. " +
                            "If you need help, please type <help> to see the game commands.",
                    authData.username(), playerColor, pieceType,
                    move.getStartPosition().toString(), move.getEndPosition().toString());
            ServerMessage moveNotification = new NotificationMessage(moveMessage);
            broadcastMessage(game.gameID(), moveNotification, session);

            ChessGame.TeamColor opponentColor = chessGame.getOpponentColor(piece.getTeamColor());
            if (chessGame.isInCheckmate(opponentColor)) {
                broadcastMessage(game.gameID(), new NotificationMessage("Checkmate! " +
                        opponentColor.name().toLowerCase() + " loses. Game over."), null);
            } else if (chessGame.isInStalemate(opponentColor)) {
                broadcastMessage(game.gameID(), new NotificationMessage("Stalemate! The game is a draw."),
                        null);
            } else if (chessGame.isInCheck(opponentColor)) {
                broadcastMessage(game.gameID(), new NotificationMessage(opponentColor.name().toLowerCase() +
                        " is in check."), null);
            }

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

        String message = String.format("%s has left the game as %s. Please type <help> to see the game commands.", leavingUser, role);
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
            sendError(new ErrorMessage("The game is already over. Please type <help> to see the game commands."), session);
            return;
        }

        String resigningUser = authData.username();

        if (!resigningUser.equals(game.whiteUsername()) && !resigningUser.equals(game.blackUsername())) {
            sendError(new ErrorMessage("Observers cannot resign the game. Please type <help> to see the game commands."), session);
            return;
        }

        chessGame.setGameOver(true);

        GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        gameDataSQLDataAccess.updateGame(updatedGame);

        String playerColor = resigningUser.equals(game.whiteUsername()) ? "white" : "black";

        String resigningUserMessage = String.format("You have resigned. The game is over. " +
                "Please type <help> to see the game commands.");
        ServerMessage resigningUserNotification = new NotificationMessage(resigningUserMessage);
        sendMessage(resigningUserNotification, session);

        String message = String.format("The %s player, %s has resigned. The game is over. " +
                "Please type <help> to see the game commands.", playerColor, resigningUser);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);
        //Not so sure about this.
        //session.close();
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

