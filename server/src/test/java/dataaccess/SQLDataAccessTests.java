package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataAccessTests {
    private UserDataDataAccess userDataAccess;
    private AuthDataDataAccess authDataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
    }

    //UserData SQL Tests
    @ParameterizedTest
    @ValueSource(classes = {UserDataSQLDataAccess.class, UserDataMemoryDataAccess.class})
    void createUser(Class<? extends UserDataDataAccess> dbClass) throws DataAccessException {
        userDataAccess = getUserDataAccess(dbClass);

        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> userDataAccess.createUser(user));

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {UserDataSQLDataAccess.class, UserDataMemoryDataAccess.class})
    void existingUser(Class<? extends UserDataDataAccess> dbClass) throws DataAccessException {
        userDataAccess = getUserDataAccess(dbClass);

        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> userDataAccess.createUser(user));
        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> userDataAccess.createUser(repeatedUser));

        UserData retrievedUser = userDataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }

    //AuthData Test
    @ParameterizedTest
    @ValueSource(classes = {AuthDataSQLDataAccess.class, AuthDataMemoryDataAccess.class})
    void generateAuthData(Class<? extends AuthDataDataAccess> dbClass) throws DataAccessException {
        authDataAccess = getAuthDataAccess(dbClass);
        String username = "Benjamin";
        String authToken = authDataAccess.createAuth(username);
        AuthData authData = authDataAccess.getAuth(authToken);

        assertEquals(username, authData.username());
        assertEquals(authToken, authData.authToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {AuthDataSQLDataAccess.class, AuthDataMemoryDataAccess.class})
    void getAuthDataWithNullUser(Class<? extends AuthDataDataAccess> dbClass) throws DataAccessException {
        authDataAccess = getAuthDataAccess(dbClass);
        assertThrows(DataAccessException.class, () -> {
            authDataAccess.createAuth(null);
        }, "Data exception. Null user");
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