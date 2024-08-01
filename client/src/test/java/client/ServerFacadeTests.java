package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        String serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + serverUrl);
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    //Register
    @Test
    @Order(1)
    @DisplayName("Successful register")
    void successfulRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire",
                "ben@gmail.com");
        RegisterResult authData = facade.register(registerRequest);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @Order(2)
    @DisplayName("Register with duplicate data")
    void registerWithDuplicateData() throws Exception {
        RegisterRequest validRequest = new RegisterRequest("bensito", "chiguire",
                "ben@gmail.com");
        Assertions.assertDoesNotThrow(() -> facade.register(validRequest));

        RegisterRequest duplicateRequest = new RegisterRequest("bensito", "chiguire",
                "ben@gmail.com");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () -> facade.register(duplicateRequest));

        assertTrue(exception.getMessage().contains("already taken"));
    }

    //Login
    @Test
    @Order(3)
    @DisplayName("Successful login")
    void loginSuccessfully() throws Exception {
        RegisterRequest validRegisterRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        Assertions.assertDoesNotThrow(() -> facade.register(validRegisterRequest));

        LoginRequest validLoginRequest = new LoginRequest("bensito", "chiguire");
        LoginResult result = facade.login(validLoginRequest);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    @Order(4)
    @DisplayName("Login with non-existent user")
    void loginWithNonExistentUser() throws Exception {
        LoginRequest nonExistentUserLoginRequest = new LoginRequest("nonExistentUser", "nonExistentPass");
        Exception thrownException = null;
        try {
            facade.login(nonExistentUserLoginRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Error: unauthorize"));
    }

}
