package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketFacade extends Endpoint {
    private Session session;
    private GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler gameHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                    ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(jsonObject.get("serverMessageType").getAsString());

                    ServerMessage serverMessage;
                    switch (type) {
                        case LOAD_GAME:
                            serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            break;
                        case ERROR:
                            serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                            break;
                        case NOTIFICATION:
                            serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown message type: " + type);
                    }

                    gameHandler.processMessage(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        gameHandler.onOpen(session);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        gameHandler.onClose(session, closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        gameHandler.onError(session, thr);
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        if (session == null) {
            throw new ResponseException(500, "WebSocket connection is not established.");
        }
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(UserGameCommand command) throws ResponseException {
        sendMessage(command);
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException {
        if (session == null) {
            throw new ResponseException(500, "WebSocket connection is not established.");
        }
        try {
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resignGame(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void sendMessage(UserGameCommand command) throws ResponseException {
        if (session == null) {
            throw new ResponseException(500, "WebSocket connection is not established.");
        }
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void close() throws IOException {
        if (session != null) {
            this.session.close();
        }
    }
}