package dataaccess;

import model.GameData;

import java.util.Collection;

public class GameDataSQLDataAccess implements GameDataDataAccess{
    @Override
    public int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() {

    }
}
