package server.websocket;

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

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private GameDataSQLDataAccess gameDataSQLDataAccess = new GameDataSQLDataAccess();
    private AuthDataSQLDataAccess authDataSQLDataAccess = new AuthDataSQLDataAccess();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Session connected.");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Session closed.");
        sessions.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String string) throws IOException {
        // Log the received message
        System.out.println("Message received: " + string);
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(string, UserGameCommand.class);
            System.out.println("Deserialized command: " + userGameCommand);

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


    //No more messages that it should receive!.
    //No JSON but actual message. TO-DO.

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = validateAuthToken(command.getAuthToken(), session);
        if (authData == null) return;

        GameData game = validateGameID(command.getGameID(), session);
        if (game == null) return;

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        sessions.addSessionToGame(game.gameID(), session);

        String role;
        if (authData.username().equals(whiteUsername)) {
            role = "white player";
        } else if (authData.username().equals(blackUsername)) {
            role = "black player";
        } else {
            role = "observer";
        }

        ServerMessage loadGameMessage = new LoadGameMessage(game);
        sendMessage(loadGameMessage, session);

        String message = String.format("%s has joined the game as %s.", authData.username(), role);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(game.gameID(), notification, session);
    }


    private void makeMove(UserGameCommand command, Session session) {
        // Placeholder for makeMove logic
    }

    private void leaveGame(UserGameCommand command, Session session) {
        //
    }

    private void resignGame(UserGameCommand command, Session session) throws IOException, DataAccessException {
        //
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

