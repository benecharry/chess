package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import exception.AlreadyTakenException;
import exception.UnauthorizedException;
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

    public JoinGameResult joingame(JoinGameRequest request) throws DataAccessException, UnauthorizedException, AlreadyTakenException {
        String authToken = request.authToken();
        System.out.println("Processing authToken: " + authToken);
        AuthData authData = authDataDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Auth token does not exist");
        }

        int gameID = request.gameID();
        System.out.println("Processing gameID: " + gameID);
        GameData gameData = gameDataDataAccess.getGame(gameID);
        if (gameData == null) {
            throw new IllegalArgumentException("Game does not exist");
        }

        String username = authData.username();
        String playerColor = request.playerColor();
        System.out.println("Processing playerColor: " + playerColor);

        if (playerColor == null) {
            throw new IllegalArgumentException("Invalid player color");
        }

        switch (playerColor.toUpperCase()) {
            case "WHITE":
                if (gameData.whiteUsername() != null && !gameData.whiteUsername().isEmpty()) {
                    throw new AlreadyTakenException("White is already taken");
                }
                gameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
                break;
            case "BLACK":
                if (gameData.blackUsername() != null && !gameData.blackUsername().isEmpty()) {
                    throw new AlreadyTakenException("Black is already taken");
                }
                gameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                break;
            default:
                throw new IllegalArgumentException("Invalid player color");
        }

        gameDataDataAccess.updateGame(gameData);

        return new JoinGameResult();
    }
}