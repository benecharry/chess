package service;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import exception.AlreadyTakenException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceUnitTest {
    private UserDataMemoryDataAccess userDataDataAccess;
    private AuthDataMemoryDataAccess authDataDataAccess;
    private RegisterService registerService;

    @BeforeEach
    public void setup() {
        userDataDataAccess = new UserDataMemoryDataAccess();
        authDataDataAccess = new AuthDataMemoryDataAccess();
        registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
    }

    // Register Service Tests
    @Test
    @DisplayName("Username Already Exists")
    public void testExisingUsername() throws Exception{
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        UserData existingUser = new UserData("username", "password", "email");
        userDataDataAccess.createUser(existingUser);
        //To verify that the user is in fact existing.
        UserData retrievedUser = userDataDataAccess.getUser("username");
        AlreadyTakenException exception = assertThrows(AlreadyTakenException.class, () -> {
            registerService.register(request);
        });
        assertEquals("Username already exists.", exception.getMessage());
    }

}