package dataaccess;

import model.AuthData;

public interface AuthDataDataAccess {
    String createAuth(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clear();
}
