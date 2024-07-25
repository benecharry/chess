package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDataSQLDataAccess implements UserDataDataAccess {
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) != null) {
            throw new DataAccessException("Username " + userData.username() + " already in use.");
        }
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DatabaseInitializer.executeUpdate(statement, false, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        //Using the same formatting as petshop was breaking my previous code.
        var statement = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"),
                                rs.getString("email"));
                    }
                 }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        DatabaseInitializer.executeUpdate(statement, false);
    }

    @Override
    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        UserData user = getUser(username);
        if (user == null) {
        return false;
        }
        return BCrypt.checkpw(providedClearTextPassword, user.password());
    }
}

