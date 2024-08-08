package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class WebSocketSessions {
    private final Map<Integer, Set<Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(int gameID, Session session) {
        sessionMap.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        Set<Session> sessions = sessionMap.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }

    public void removeSession(Session session) {
        for (Set<Session> sessions : sessionMap.values()) {
            sessions.remove(session);
        }
        sessionMap.values().removeIf(Set::isEmpty);
    }

    public Set<Session> getSessionsForGame(int gameID) {
        return sessionMap.getOrDefault(gameID, Collections.emptySet());
    }
}