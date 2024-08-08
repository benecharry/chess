package websocket;

import websocket.messages.ServerMessage;
import javax.websocket.Session;
import javax.websocket.CloseReason;

public interface GameHandler {
    void onOpen(Session session);
    void onClose(Session session, CloseReason closeReason);
    void onError(Session session, Throwable thr);
    void processMessage(ServerMessage serverMessage);
}