package handler;

import exception.AlreadyTakenException;
import exception.UnauthorizedException;
import model.AuthData;
import model.GameData;

public class ValidationHandler {
    public static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void checkAuthData(AuthData authData) throws UnauthorizedException {
        if (authData == null) {
            throw new UnauthorizedException("Invalid auth token");
        }
    }
    public static void checkGameData(GameData gameData) {
        if (gameData == null) {
            throw new IllegalArgumentException("Game does not exist");
        }
    }
    public static void checkAlreadyTaken(boolean isTaken, String message) throws AlreadyTakenException {
        if (isTaken) {
            throw new AlreadyTakenException(message);
        }
    }

    public static void setDefaultUsernamesIfEmpty(String[] usernames, String defaultWhiteUsername, String defaultBlackUsername) {
        if (usernames[0] == null || usernames[0].isEmpty()) {
            usernames[0] = defaultWhiteUsername;
        }
        if (usernames[1] == null || usernames[1].isEmpty()) {
            usernames[1] = defaultBlackUsername;
        }
    }
}
