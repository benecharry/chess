package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private GameData game;
    private String role;

    public LoadGameMessage(GameData game, String role) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.role = role;
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

    @Override
    public String toString() {
        return String.format("You just joined the game with ID %d as the %s.", game.gameID(), role);

    }
}
