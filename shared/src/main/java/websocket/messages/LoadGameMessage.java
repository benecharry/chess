package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private GameData game;
    private String role;
    private boolean isJoinNotification;

    public LoadGameMessage(GameData game, String role) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.role = role;
    }

    public LoadGameMessage(GameData game, String role, boolean isJoinNotification) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.role = role;
        this.isJoinNotification = isJoinNotification;
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isJoinNotification() {
        return isJoinNotification;
    }

    public void setJoinNotification(boolean joinNotification) {
        isJoinNotification = joinNotification;
    }

    @Override
    public String toString() {
        if (isJoinNotification) {
            return String.format("You have joined the game with ID %d as the %s.", game.gameID(), role);
        } else {
            return "Game loaded";
        }
    }
}