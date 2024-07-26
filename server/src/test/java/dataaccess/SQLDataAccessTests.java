package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName("Create User")
    void createUserPositive() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> userDataAccess.createUser(user));

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }

    @Test
    @DisplayName("Existing User")
    void existingUser() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> userDataAccess.createUser(repeatedUser));
    }

    //Login SQL tests
    @Test
    @DisplayName("Login Successful")
    void loginSuccessful() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());

        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> userDataAccess.createUser(repeatedUser));
    }

    @Test
    @DisplayName("Wrong password")
    void wrongPasswor() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        boolean passwordVerified = userDataAccess.verifyUser("Benjamin", "duck");
        assertFalse(passwordVerified, "Verification with should fail");
    }

    //Logout SQL tests
    @Test
    @DisplayName("Successful logout")
    void successfulLogout() throws DataAccessException {
        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        userDataAccess.createUser(user);

        String authToken = authDataAccess.createAuth(user.username());
        authDataAccess.deleteAuth(authToken);
        AuthData deletedAuthData = authDataAccess.getAuth(authToken);

        assertNull(deletedAuthData, "After logout authData should be deleted");
    }

    @Test
    @DisplayName("Invalid Logout due to false authToken")
    void invalidLogoutFalseToken() throws DataAccessException {
        String falseToken = "false-token";
        assertDoesNotThrow(() -> authDataAccess.deleteAuth(falseToken),
                "False token deleted");

        AuthData authData = authDataAccess.getAuth(falseToken);
        assertNull(authData, "AuthData should be null for false token");
    }

    //CreateGame SQL tests

    //ListGames SQL tests

    //JoinGame SQL tests

    //Clear SQL tests

    @Test
    @DisplayName("Clear Successful")
    void clearSuccessful() throws DataAccessException{
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

    private UserDataDataAccess getUserDataAccess(Class<? extends UserDataDataAccess> databaseClass) throws DataAccessException {
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
    }
}