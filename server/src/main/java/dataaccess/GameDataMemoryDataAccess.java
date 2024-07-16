package dataaccess;

import model.GameData;

import java.util.Collection;

public class GameDataMemoryDataAccess implements GameDataDataAccess{
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
}
