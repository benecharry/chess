package result;

//games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""}

import chess.ChessGame;
import java.util.List;

public record ListGamesResult(List<GameDetails> games) {
    public static record GameDetails(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
}