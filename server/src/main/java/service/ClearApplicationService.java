package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import dataaccess.UserDataDataAccess;
import request.ClearApplicationRequest;
import result.ClearApplicationResult;

public class ClearApplicationService {
    private final UserDataDataAccess userDataDataAccess;
    private final AuthDataDataAccess authDataDataAccess;
    private final GameDataDataAccess gameDataDataAccess;

    public ClearApplicationService(UserDataDataAccess userDataDataAccess, AuthDataDataAccess authDataDataAccess,
                                   GameDataDataAccess gameDataDataAccess) {
        this.userDataDataAccess = userDataDataAccess;
        this.authDataDataAccess = authDataDataAccess;
        this.gameDataDataAccess = gameDataDataAccess;
    }

    public ClearApplicationResult clearApplication(ClearApplicationRequest request) throws DataAccessException{
        userDataDataAccess.clear();
        authDataDataAccess.clear();
        gameDataDataAccess.clear();
        return new ClearApplicationResult();
    }
}
