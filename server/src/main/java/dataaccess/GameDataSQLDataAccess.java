package dataaccess;

import chess.ChessGame;
import handler.SerializationHandler;
import handler.ValidationHandler;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GameDataSQLDataAccess implements GameDataDataAccess {

    @Override
    public int createGame(String gameName, String whiteUsername, String blackUsername) throws DataAccessException {
        ValidationHandler.checkNotNull(gameName, "Game name cannot be null");
        whiteUsername = ValidationHandler.emptyToNull(whiteUsername);
        blackUsername = ValidationHandler.emptyToNull(blackUsername);

        String gameString = SerializationHandler.toJson(new ChessGame());
        var statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        return DatabaseInitializer.executeUpdate(statement, true, gameName, whiteUsername, blackUsername, gameString);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readGameData(rs);
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
            var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameData(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
        return result;
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        String gameString = SerializationHandler.toJson(gameData.game());
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        int affectedRows = DatabaseInitializer.executeUpdate(statement, false, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameString, gameData.gameID());

        if (affectedRows == 0) {
            throw new DataAccessException("No game found with ID: " + gameData.gameID());
        }
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var gameName = rs.getString("gameName");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        String gameJson = rs.getString("game");
        ChessGame game = SerializationHandler.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        DatabaseInitializer.executeUpdate(statement, false);
    }
}

