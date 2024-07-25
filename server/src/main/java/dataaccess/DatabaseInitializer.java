package dataaccess;

import chess.ChessGame;
import handler.SerializationHandler;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseInitializer {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authTokens (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(256) NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static int executeUpdate(String statement, boolean returnGeneratedKeys, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = returnGeneratedKeys ? conn.prepareStatement(statement, RETURN_GENERATED_KEYS)
                     : conn.prepareStatement(statement)) {
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param instanceof ChessGame p) ps.setString(i + 1, SerializationHandler.toJson(p));
                else if (param == null) ps.setNull(i + 1, NULL);
            }
            ps.executeUpdate();
            if (returnGeneratedKeys) {
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}