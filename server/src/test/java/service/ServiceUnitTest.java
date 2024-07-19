package service;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import exception.AlreadyTakenException;
import exception.UnauthorizedException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.ClearApplicationRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.ClearApplicationResult;
import result.RegisterResult;
import request.LoginRequest;
import result.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTest {
    private UserDataMemoryDataAccess userDataDataAccess;
    private AuthDataMemoryDataAccess authDataDataAccess;
    private GameDataMemoryDataAccess gameDataMemoryDataAccess;
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ClearApplicationService clearApplicationService;

    @BeforeEach
    public void setup() {
        userDataDataAccess = new UserDataMemoryDataAccess();
        authDataDataAccess = new AuthDataMemoryDataAccess();
        gameDataMemoryDataAccess = new GameDataMemoryDataAccess();
        registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        loginService = new LoginService(userDataDataAccess, authDataDataAccess);
        logoutService = new LogoutService(authDataDataAccess);
        clearApplicationService = new ClearApplicationService(userDataDataAccess, authDataDataAccess,
                gameDataMemoryDataAccess);
    }

    // Register Service Tests
    @Test
    @DisplayName("Successful Registration")
    public void testSuccessfulRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest("benecharry", "Naruto123$",
                "benecharry@gmail.com");
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

    //Login Service Tests
    @Test
    @DisplayName("Login Missing Password")
    public void testLoginMissingPassword() {
        LoginRequest loginRequest = new LoginRequest("username", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.login(loginRequest);
        });
        assertEquals("Missing required parameter", exception.getMessage());
    }

    @Test
    @DisplayName("Login Wrong Password")
    public void testLoginWrongPassword() throws DataAccessException {
        UserData existingUser = new UserData("username", "correctPassword", "email");
        userDataDataAccess.createUser(existingUser);

        LoginRequest loginRequest = new LoginRequest("username", "wrongPassword");
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            loginService.login(loginRequest);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }

    //Logout Service Tests
    @Test
    @DisplayName("Successful Logout")
    public void testSuccessfulLogout() throws Exception {
        UserData user = new UserData("username", "password", "email");
        userDataDataAccess.createUser(user);
        String authToken = authDataDataAccess.createAuth(user.username());
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = logoutService.logout(request);
        assertNotNull(result);
        assertNull(authDataDataAccess.getAuth(authToken));
    }

    @Test
    @DisplayName("Invalid Logout")
    public void testInvalidLogout() throws DataAccessException, UnauthorizedException {
        UserData user = new UserData("username", "password", "email");
        userDataDataAccess.createUser(user);
        String authToken = authDataDataAccess.createAuth(user.username());
        LogoutRequest firstLogoutRequest = new LogoutRequest(authToken);
        LogoutResult firstLogoutResult = logoutService.logout(firstLogoutRequest);
        assertNotNull(firstLogoutResult);
        LogoutRequest secondTry = new LogoutRequest(authToken);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            logoutService.logout(secondTry);
        });

        assertEquals("Invalid auth token", exception.getMessage());
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