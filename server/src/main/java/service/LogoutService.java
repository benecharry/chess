package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import exception.UnauthorizedException;
import handler.ValidationHandler;
import model.AuthData;
import request.LogoutRequest;
import result.LogoutResult;

public class LogoutService {
    //Find authToken and delete it.
    //Add auth token.
    //private final UserDataDataAccess userDataDataAccess;
    private final AuthDataDataAccess authDataDataAccess;

    public LogoutService(AuthDataDataAccess authDataDataAccess) {
        this.authDataDataAccess = authDataDataAccess;
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException, UnauthorizedException{
        String authToken = request.authToken();
        AuthData authData = authDataDataAccess.getAuth(authToken);
        ValidationHandler.checkAuthData(authData);

        authDataDataAccess.deleteAuth(authToken);
        return new LogoutResult();
    }
}
