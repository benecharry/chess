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
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                serverMessageHandler.notify(serverMessage);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID, String playerColor) throws ResponseException {
        if (session == null) {
            throw new ResponseException(500, "WebSocket connection is not established.");
        }
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            String connectMessage = new Gson().toJson(userGameCommand);
            System.out.println("Sending connect command: " + connectMessage); // Log outgoing messages
            this.session.getBasicRemote().sendText(connectMessage);
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