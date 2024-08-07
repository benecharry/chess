package server.websocket;

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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, command);
                break;
            case MAKE_MOVE:
                handleMakeMove(session, command);
                break;
            case LEAVE:
                handleLeave(session, command);
                break;
            case RESIGN:
                handleResign(session, command);
                break;
            default:
                sendErrorMessage(session, "Unknown command type: " + command.getCommandType());
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        sendNotificationMessage(session, "User connected to game " + command.getGameID());
    }
}