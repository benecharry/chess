package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import exception.UnauthorizedException;
import handler.ValidationHandler;
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
        String gameName = request.gameName();
        ValidationHandler.checkNotNull(gameName, "Name can't be empty");
        String authToken = request.authToken();
        AuthData authData = authDataDataAccess.getAuth(authToken);
        ValidationHandler.checkAuthData(authData);

        try {
            int gameID = gameDataDataAccess.createGame(gameName, "", "");
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Name is already in use");
        }
    }
}