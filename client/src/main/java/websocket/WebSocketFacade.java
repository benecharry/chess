package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketFacade extends Endpoint {
    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler gameHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                gameHandler.processMessage(serverMessage);
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

    public void leaveGame(UserGameCommand command) throws ResponseException {
        sendMessage(command);
    }

    public void resignGame(UserGameCommand command) throws ResponseException {
        sendMessage(command);
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