package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import exception.AlreadyTakenException;
import exception.UnauthorizedException;
import handler.ValidationHandler;
import model.GameData;
import model.AuthData;
import request.JoinGameRequest;
import result.JoinGameResult;

public class JoinGameService {
    private final AuthDataDataAccess authDataDataAccess;
    private final GameDataDataAccess gameDataDataAccess;

    public JoinGameService(AuthDataDataAccess authDataDataAccess, GameDataDataAccess gameDataDataAccess) {
        this.authDataDataAccess = authDataDataAccess;
        this.gameDataDataAccess = gameDataDataAccess;
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException, UnauthorizedException, AlreadyTakenException {
        String authToken = request.authToken();
        AuthData authData = authDataDataAccess.getAuth(authToken);
        ValidationHandler.checkAuthData(authData);

        int gameID = request.gameID();
        GameData gameData = gameDataDataAccess.getGame(gameID);
        ValidationHandler.checkGameData(gameData);

        String username = authData.username();
        String playerColor = request.playerColor();
        ValidationHandler.checkNotNull(playerColor, "Invalid player color");

        switch (playerColor.toUpperCase()) {
            case "WHITE":
                ValidationHandler.checkAlreadyTaken(gameData.whiteUsername() != null &&
                        !gameData.whiteUsername().isEmpty(), "White is already taken");
                gameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
                break;
            case "BLACK":
                ValidationHandler.checkAlreadyTaken(gameData.blackUsername() != null &&
                        !gameData.blackUsername().isEmpty(), "Black is already taken");
                gameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                break;
            default:
                throw new IllegalArgumentException("Invalid player color");
        }

        gameDataDataAccess.updateGame(gameData);

        return new JoinGameResult();
    }
}