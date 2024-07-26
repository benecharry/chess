package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.GameData;
import chess.*;
import request.JoinGameRequest;
import service.JoinGameService;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataAccessTests {
    private UserDataDataAccess userDataAccess;
    private AuthDataDataAccess authDataAccess;
    private GameDataDataAccess gameDataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDataAccess = new UserDataSQLDataAccess();
        authDataAccess = new AuthDataSQLDataAccess();
        gameDataAccess = new GameDataSQLDataAccess();

        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();
    }

    //Register SQL Tests
    @Test
    @DisplayName("Create User Test")
    void createUserPositiveTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> userDataAccess.createUser(user));

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }

    @Test
    @DisplayName("Existing User Test")
    void existingUserTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> userDataAccess.createUser(repeatedUser));
    }

    //Login SQL tests
    @Test
    @DisplayName("Login Successful test")
    void loginSuccessfulTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());

        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> userDataAccess.createUser(repeatedUser));
    }

    @Test
    @DisplayName("Wrong password test")
    void wrongPasswordTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        boolean passwordVerified = userDataAccess.verifyUser("Benjamin", "duck");
        assertFalse(passwordVerified, "Verification with should fail");
    }

    //Logout SQL tests
    @Test
    @DisplayName("Successful logout test")
    void successfulLogoutTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        String authToken = authDataAccess.createAuth(user.username());
        authDataAccess.deleteAuth(authToken);
        AuthData deletedAuthData = authDataAccess.getAuth(authToken);
        assertNull(deletedAuthData, "After logout authData should be deleted");
    }

    @Test
    @DisplayName("Invalid Logout due to false authToken test")
    void invalidLogoutFalseTokenTest() throws DataAccessException {
        String falseToken = "false-token";
        assertDoesNotThrow(() -> authDataAccess.deleteAuth(falseToken),
                "False token deleted");

        AuthData authData = authDataAccess.getAuth(falseToken);
        assertNull(authData, "AuthData should be null for false token");
    }

    //CreateGame SQL tests
    @Test
    @DisplayName("Create game successful")
    void createGameSuccess() throws DataAccessException {
        int gameID = gameDataAccess.createGame("New Game", "Benjamin",
                "Samuel");
        GameData initialGameData = gameDataAccess.getGame(gameID);
        ChessGame game = initialGameData.game();

        assertEquals("New Game", initialGameData.gameName(), "Game name should be New Game");
        assertEquals("Benjamin", initialGameData.whiteUsername(), "White player should be 'Benjamin'");
        assertEquals("Samuel", initialGameData.blackUsername(), "Black player should be 'Samuel'");
        assertNotNull(game, "Chess game should not be null");
    }

    @Test
    @DisplayName("Create game with null game name")
    void createGameWithNullGameName() throws DataAccessException, IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            gameDataAccess.createGame(null, "Benjamin", "Samuel");
        });
    }

    //ListGames SQL tests
    @Test
    @DisplayName("Successful game list test")
    void listGameTest() throws DataAccessException {
        int firstGameID = gameDataAccess.createGame("1st Game", "Benjamin",
                "Samuel");
        GameData firstGameData = gameDataAccess.getGame(firstGameID);
        ChessGame firstGame = firstGameData.game();

        int secondGameID = gameDataAccess.createGame("2nd Game", "Sarah", "Jose");
        GameData secondGameData = gameDataAccess.getGame(secondGameID);
        ChessGame secondGame = secondGameData.game();

        Collection<GameData> gamesList = gameDataAccess.listGames();

        assertTrue(gamesList.stream().anyMatch(game ->
                game.gameID() == firstGameID &&
                        game.gameName().equals("1st Game") &&
                        game.whiteUsername().equals("Benjamin") &&
                        game.blackUsername().equals("Samuel") &&
                        game.game().equals(firstGame)
        ), "First game details are incorrect");

        assertTrue(gamesList.stream().anyMatch(game ->
                game.gameID() == secondGameID &&
                        game.gameName().equals("2nd Game") &&
                        game.whiteUsername().equals("Sarah") &&
                        game.blackUsername().equals("Jose") &&
                        game.game().equals(secondGame)
        ), "Second game details are incorrect");
    }

    @Test
    @DisplayName("No games to list test")
    void noGamesList() throws DataAccessException{
        gameDataAccess.clear();
        Collection<GameData> gamesList = gameDataAccess.listGames();
        assertTrue(gamesList.isEmpty(), "The games list is empty");
    }

    //JoinGame SQL tests
    @Test
    @DisplayName("Update Game test")
    void updateGameTest() throws DataAccessException, InvalidMoveException {
        int gameID = gameDataAccess.createGame("New Game", "Benjamin",
                "Samuel");
        GameData initialGameData = gameDataAccess.getGame(gameID);
        ChessGame game = initialGameData.game();

        ChessPosition start = new ChessPosition(2, 5);
        ChessPosition end = new ChessPosition(4, 5);

        ChessMove move = new ChessMove(start, end, null);
        game.makeMove(move);

        GameData updatedGameData = new GameData(gameID, "Benjamin", "Samuel",
                "Active Game", game);
        gameDataAccess.updateGame(updatedGameData);

        GameData newGameID = gameDataAccess.getGame(gameID);
        assertEquals("Active Game", newGameID.gameName(), "Updated name");
        assertEquals(game, newGameID.game(), "Move was added");
    }

    @Test
    @DisplayName("Join Game with invalid game test")
    void joinGameInvalidGameIdTest() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);
        String authToken = authDataAccess.createAuth("Benjamin");

        JoinGameService joinGameService = new JoinGameService(authDataAccess, gameDataAccess);

        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", -1, authToken);
        assertThrows(IllegalArgumentException.class, () -> {
            joinGameService.joinGame(joinRequest);
        });
    }

    //Clear SQL tests
    @Test
    @DisplayName("Clear successful test")
    void clearSuccessfulTest() throws DataAccessException{
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);
        String authToken = authDataAccess.createAuth("Benjamin");
        gameDataAccess.createGame("New Game", "Benjamin", "Samuel");

        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();

        assertNull(userDataAccess.getUser("Benjamin"));
        assertNull(authDataAccess.getAuth(authToken));
        assertTrue(gameDataAccess.listGames().isEmpty());
    }

/*    private UserDataDataAccess getUserDataAccess(Class<? extends UserDataDataAccess> databaseClass) throws DataAccessException {
        UserDataDataAccess db;
        if (databaseClass.equals(UserDataSQLDataAccess.class)) {
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.configureDatabase();
            db = new UserDataSQLDataAccess();
        } else {
            db = new UserDataMemoryDataAccess();
        }
        db.clear();
        return db;
    }

    private AuthDataDataAccess getAuthDataAccess(Class<? extends AuthDataDataAccess> databaseClass) throws DataAccessException {
        AuthDataDataAccess db;
        if (databaseClass.equals(AuthDataSQLDataAccess.class)) {
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.configureDatabase();
            db = new AuthDataSQLDataAccess();
        } else {
            db = new AuthDataMemoryDataAccess();
        }
        db.clear();
        return db;
    }*/
}