package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDataDataAccess {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
