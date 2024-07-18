package service;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.GameDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import exception.AlreadyTakenException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.ClearApplicationRequest;
import request.RegisterRequest;
import result.ClearApplicationResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTest {
    private UserDataMemoryDataAccess userDataDataAccess;
    private AuthDataMemoryDataAccess authDataDataAccess;
    private GameDataMemoryDataAccess gameDataMemoryDataAccess;
    private RegisterService registerService;
    private ClearApplicationService clearApplicationService;

    @BeforeEach
    public void setup() {
        userDataDataAccess = new UserDataMemoryDataAccess();
        authDataDataAccess = new AuthDataMemoryDataAccess();
        gameDataMemoryDataAccess = new GameDataMemoryDataAccess();
        registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        clearApplicationService = new ClearApplicationService(userDataDataAccess, authDataDataAccess, gameDataMemoryDataAccess);
    }

    // Register Service Tests
    @Test
    @DisplayName("Successful Registration")
    public void testSuccessfulRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest("benecharry", "Naruto123$", "benecharry@gmail.com");
        RegisterResult result = registerService.register(request);

        UserData registeredUser = userDataDataAccess.getUser("benecharry");
        assertNotNull(registeredUser);
        assertEquals("benecharry", registeredUser.username());
        assertEquals("benecharry@gmail.com", registeredUser.email());

        assertNotNull(result);
        assertEquals("benecharry", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Username Already Exists")
    public void testExisingUsername() throws Exception{
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        UserData existingUser = new UserData("username", "password", "email");
        userDataDataAccess.createUser(existingUser);
        //To verify that the user is in fact existing.
        UserData retrievedUser = userDataDataAccess.getUser("username");
        assertEquals(existingUser, retrievedUser);
        AlreadyTakenException exception = assertThrows(AlreadyTakenException.class, () -> {
            registerService.register(request);
        });
        assertEquals("Username is already taken.", exception.getMessage());
    }

    //Clear Service Tests
    @Test
    @DisplayName("Successful Clear")
    public void testSuccessfulClear() throws Exception {
        userDataDataAccess.createUser(new UserData("username", "password", "email"));
        authDataDataAccess.createAuth("username");
        //TO DO
        // Add game when implemented cause is not working as for right now.
        ClearApplicationRequest request = new ClearApplicationRequest();
        ClearApplicationResult result = clearApplicationService.clearApplication(request);

        assertNotNull(result);
        assertNull(userDataDataAccess.getUser("username"));
        assertNull(authDataDataAccess.getAuth("username"));
    }


}