package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT:
                connect(userGameCommand, session);
                break;
            case MAKE_MOVE:
                makeMove(userGameCommand, session);
                break;
            case LEAVE:
                leave(userGameCommand, session);
                break;
            case RESIGN:
                resign(userGameCommand, session);
                break;
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        connections.add(authToken, session);
        ServerMessage loadGameMessage = new ServerMessage(ServerMessageType.LOAD_GAME);

        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        String message = String.format("%s has joined the game.", authToken);
        //System.out.println("Message to be sent: " + message);

        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(authToken, notification);
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException {
        // Implementation for makeMove
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        // Implementation for leave
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        // Implementation for resign
    }
}
