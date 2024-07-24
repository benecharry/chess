package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class GameDataSQLDataAccess implements GameDataDataAccess{
    private int nextId = 1;
    @Override
    public int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException {
        int gameID = nextId++;
        ChessGame game = new ChessGame();
        var statement = "INSERT INTO users (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        DatabaseInitializer.executeUpdate(statement, gameID, whiteUsername, blackUsername, gameName, gameString);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    //return new GameData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }
    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        DatabaseInitializer.executeUpdate(statement);
    }
}
