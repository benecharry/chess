package dataaccess;

import model.AuthData;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class GameDataMemoryDataAccess implements GameDataDataAccess{
    private final HashMap<String, AuthData> games = new HashMap<>();
    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear(){
        games.clear();
    }
}
