package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class AuthDataMemoryDataAccess implements AuthDataDataAccess{
    private final HashMap<String, AuthData> authTokens = new HashMap<>();
    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        authTokens.put(authToken, new AuthData(authToken, username));
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

    @Override
    public void clear(){
        authTokens.clear();
    }
}
