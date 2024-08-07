package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
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
                connect(session, userGameCommand);
                break;
            case MAKE_MOVE:
                makeMove(session, userGameCommand);
                break;
            case LEAVE:
                leave(session, userGameCommand);
                break;
            case RESIGN:
                resign(session, userGameCommand);
                break;
            default:
                sendErrorMessage(session, "Unknown command type: " + userGameCommand.getCommandType());
        }
    }

    //Missing
    //        LOAD_GAME,
    //        ERROR,
    //        NOTIFICATION

    //All server messages must include a
    // serverMessageType field and should
    // inherit from the ServerMessage class.
    // This field must be set to the corresponding ServerMessageType.

    private void connect(Session session, UserGameCommand command) throws IOException {
        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION);
        notification.setMessage(command.getAuthToken() + " has joined the game.");
        connections.broadcast(command.getAuthToken(), notification);
    }
}