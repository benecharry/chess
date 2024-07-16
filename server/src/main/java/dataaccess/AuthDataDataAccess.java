package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDataDataAccess {
    AuthData createAuth(AuthData authData) throws  DataAccessException;
    AuthData getAuth(String authToken) throws  DataAccessException;
    void deleteAuth(AuthData authData) throws DataAccessException;
}
