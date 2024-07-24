package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataAccessTests {
    private UserDataDataAccess dataAccess;
    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new UserDataSQLDataAccess();
        dataAccess.clear();
    }

    //UserData SQL Tests
    @ParameterizedTest
    @ValueSource(classes = {UserDataSQLDataAccess.class, UserDataMemoryDataAccess.class})
    void createUser(Class<? extends UserDataDataAccess> dbClass) throws DataAccessException {
        dataAccess = getDataAccess(dbClass);

        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> dataAccess.createUser(user));

        UserData retrievedUser = dataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {UserDataSQLDataAccess.class, UserDataMemoryDataAccess.class})
    void existingUser(Class<? extends UserDataDataAccess> dbClass) throws DataAccessException{
        dataAccess = getDataAccess(dbClass);

        var user = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertDoesNotThrow(() -> dataAccess.createUser(user));
        var repeatedUser = new UserData("Benjamin", "chiguire", "benecharry@icloud.com");
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(repeatedUser));

        UserData retrievedUser = dataAccess.getUser("Benjamin");
        assertNotNull(retrievedUser);
        assertEquals("Benjamin", retrievedUser.username());
        assertEquals("benecharry@icloud.com", retrievedUser.email());
    }


    private UserDataDataAccess getDataAccess(Class<? extends UserDataDataAccess> databaseClass) throws DataAccessException {
        UserDataDataAccess db;
        if (databaseClass.equals(UserDataSQLDataAccess.class)) {
            db = new UserDataSQLDataAccess();
        } else {
            db = new UserDataMemoryDataAccess();
        }
        db.clear();
        return db;
    }

}