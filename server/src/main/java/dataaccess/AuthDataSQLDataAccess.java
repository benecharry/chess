package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class AuthDataSQLDataAccess implements AuthDataDataAccess{
    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        DatabaseInitializer.executeUpdate(statement, authToken, username);
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
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
