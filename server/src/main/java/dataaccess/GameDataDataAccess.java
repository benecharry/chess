package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDataDataAccess {
    int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
