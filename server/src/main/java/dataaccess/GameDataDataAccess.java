package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDataDataAccess {
    int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException;
    GameData getGame(int gameID);
    Collection<GameData> listGames();

    void updateGame(GameData gameData);
    void clear();

}
