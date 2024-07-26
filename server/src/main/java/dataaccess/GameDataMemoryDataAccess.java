package dataaccess;

import chess.ChessGame;
import handler.ValidationHandler;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class GameDataMemoryDataAccess implements GameDataDataAccess{
    private int nextId = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();
    @Override
    public int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException {
        for (GameData game : games.values()) {
            if (game.gameName().equals(gameName)) {
                throw new DataAccessException("Name in use");
            }
        }
        whiteUsername = ValidationHandler.emptyToNull(whiteUsername);
        blackUsername = ValidationHandler.emptyToNull(blackUsername);

        int gameID = nextId++;
        ChessGame game = new ChessGame();
        games.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameName, game));
        return gameID;
    }

    @Override
    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames(){
        return games.values();
    }

    @Override
    public void updateGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear(){
        games.clear();
    }
}