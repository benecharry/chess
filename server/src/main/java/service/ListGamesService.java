package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataDataAccess;
import exception.UnauthorizedException;
import handler.ValidationHandler;
import model.AuthData;
import model.GameData;
import request.ListGamesRequest;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;

import java.util.Collection;

public class ListGamesService {
    private final AuthDataDataAccess authDataDataAccess;
    private final GameDataDataAccess gameDataDataAccess;

    public ListGamesService (AuthDataDataAccess authDataDataAccess, GameDataDataAccess gameDataDataAccess) {
        this.authDataDataAccess = authDataDataAccess;
        this.gameDataDataAccess = gameDataDataAccess;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException, UnauthorizedException {
        String authToken = request.authToken();
        AuthData authData = authDataDataAccess.getAuth(authToken);
        ValidationHandler.checkAuthData(authData);

        Collection<GameData> games = gameDataDataAccess.listGames();
        List<ListGamesResult.GameDetails> finalGameList = new ArrayList<>();

        for (GameData game : games) {
            finalGameList.add(new ListGamesResult.GameDetails(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            ));
        }

        return new ListGamesResult(finalGameList);
    }
}