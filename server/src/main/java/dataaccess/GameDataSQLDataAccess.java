package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import handler.SerializationHandler;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GameDataSQLDataAccess implements GameDataDataAccess{
    @Override
    public int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException {
        ChessGame game = new ChessGame();
        String gameString = SerializationHandler.toJson(game);
        var statement = "INSERT INTO users (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        return DatabaseInitializer.executeUpdate(statement, true, whiteUsername, blackUsername,
                gameName, gameString);
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

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        //result.add(readPet(rs));
                    }
                }
            }
        }   catch (SQLException e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        DatabaseInitializer.executeUpdate(statement, false);
    }
}
