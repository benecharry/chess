package dataaccess;

import chess.ChessGame;
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
        int gameID = nextId++;
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, new ChessGame());
        games.put(gameID, gameData);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void clear(){
        games.clear();
    }
}
