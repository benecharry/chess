package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import exception.UnauthorizedException;
import request.CreateGameRequest;
import result.CreateGameResult;
import model.AuthData;

public class CreateGameService {
    private final AuthDataDataAccess authDataDataAccess;
    private final GameDataDataAccess gameDataDataAccess;

    public CreateGameService (AuthDataDataAccess authDataDataAccess, GameDataDataAccess gameDataDataAccess) {
        this.authDataDataAccess = authDataDataAccess;
        this.gameDataDataAccess = gameDataDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, UnauthorizedException {
        String nameGame = request.gameName();
        if(nameGame == null){
            throw new IllegalArgumentException("Name can't be empty");
        }

        String authToken = request.authToken();
        AuthData authData = authDataDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Auth token does not exists");
        }

        try {
            int gameID = gameDataDataAccess.createGame(nameGame, null, null);
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Name is already in use");
        }
    }
}
