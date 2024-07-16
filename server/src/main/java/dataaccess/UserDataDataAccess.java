package dataaccess;

import model.UserData;

public interface UserDataDataAccess {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;


}
