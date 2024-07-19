package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class GameDataMemoryDataAccess implements GameDataDataAccess{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        if (games.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game ID already exists");
        }
        games.put(gameData.gameID(), gameData);
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
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear(){
        games.clear();
    }
}
