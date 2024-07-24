package dataaccess;

import model.AuthData;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDataSQLDataAccess implements AuthDataDataAccess{
    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var deleteStatement = "DELETE FROM authTokens WHERE username=?";
        DatabaseInitializer.executeUpdate(deleteStatement, username);
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        DatabaseInitializer.executeUpdate(statement, authToken, username);
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM authTokens WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authTokens WHERE authToken=?";
        DatabaseInitializer.executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE authTokens";
        DatabaseInitializer.executeUpdate(statement);
    }
}