package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDataSQLDataAccess;
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
        //Troubleshooting
        System.out.println("Message received: " + string);
        UserGameCommand userGameCommand = new Gson().fromJson(string, UserGameCommand.class);
        try {
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
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        if (authToken == null || authToken.isEmpty()) {
            sendError(new ErrorMessage("Invalid authentication token."), session);
            return;
        }

        if (gameID == null || gameID <= 0) {
            sendError(new ErrorMessage("Invalid game ID."), session);
            return;
        }

        GameData game = gameDataSQLDataAccess.getGame(gameID);
        if (game == null) {
            sendError(new ErrorMessage("Game not found."), session);
            return;
        }

        sessions.addSessionToGame(gameID, session);

        String message = String.format("%s has joined the game.", authToken);
        ServerMessage notification = new NotificationMessage(message);
        broadcastMessage(gameID, notification, session);


        ServerMessage loadGameMessage = new LoadGameMessage(game);
        sendMessage(loadGameMessage, session);
    }

    private void makeMove(UserGameCommand command, Session session) {
        // Placeholder for makeMove logic
    }

    private void leaveGame(UserGameCommand command, Session session) {
        // Placeholder for leaveGame logic
    }

    private void resignGame(UserGameCommand command, Session session) {
        // Placeholder for resignGame logic
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

