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


    //Logout
    @Test
    @Order(5)
    @DisplayName("Successful logout")
    void successfulLogout() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        assertNotNull(registerResult.authToken());

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        Exception thrownException = null;
        try {
            facade.logout(logoutRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNull(thrownException);
    }

    @Test
    @Order(6)
    @DisplayName("Logout with invalid token")
    void logoutWithInvalidToken() throws Exception {
        LogoutRequest invalidLogoutRequest = new LogoutRequest("invalidToken");
        Exception thrownException = null;
        try {
            facade.logout(invalidLogoutRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Error: unauthorized"));
    }

    //Create
    @Test
    @Order(7)
    @DisplayName("Successful game creation")
    void successfulGameCreation() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        assertNotNull(registerResult.authToken());

        CreateGameRequest createGameRequest = new CreateGameRequest("Endgame", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);

        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    @Order(8)
    @DisplayName("Create game with invalid token")
    void createGameWithInvalidToken() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest("Invalid Game", "invalidToken");
        Exception thrownException = null;

        try {
            facade.createGame(createGameRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Error: unauthorized"));
    }

    //List
    @Test
    @Order(9)
    @DisplayName("Successful list games")
    void successfulListGames() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        assertNotNull(registerResult.authToken());

        CreateGameRequest firstGameRequest = new CreateGameRequest("Game1", registerResult.authToken());
        CreateGameResult firstGameResult = facade.createGame(firstGameRequest);

        CreateGameRequest secondGameRequest = new CreateGameRequest("Game2", registerResult.authToken());
        CreateGameResult secondGameResult = facade.createGame(secondGameRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);

        assertNotNull(listGamesResult);
        assertTrue(listGamesResult.games().size() >= 2);
    }
    @Test
    @Order(10)
    @DisplayName("List games with unauthorized user")
    void createGameWithInvalidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        assertNotNull(registerResult.authToken());
        CreateGameRequest invalidCreateGameRequest = new CreateGameRequest("", registerResult.authToken());
        Exception thrownException = null;

        try {
            facade.createGame(invalidCreateGameRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Error: bad request"));
    }

    //Join
    @Test
    @Order(11)
    @DisplayName("Join game successfully")
    void joinGameSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("white", createGameResult.gameID(),
                registerResult.authToken());
        Exception thrownException = null;

        try {
            facade.joinGame(joinGameRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNull(thrownException);
    }

    @Test
    @Order(12)
    @DisplayName("Join game with invalid game ID")
    void joinGameWithInvalidGameID() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("bensito", "chiguire", "ben@gmail.com");
        RegisterResult registerResult = facade.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("white", -1, registerResult.authToken());
        Exception thrownException = null;

        try {
            facade.joinGame(joinGameRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Error: bad request"));
    }



    //Observe


}
