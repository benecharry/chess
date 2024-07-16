package dataaccess;

import model.AuthData;

public class AuthDataMemoryDataAccess implements AuthDataDataAccess{

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }
}
