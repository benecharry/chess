package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataDataAccess;
import exception.AlreadyTakenException;
import exception.UnauthorizedException;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;

import javax.xml.crypto.Data;

public class LoginService {
    //Subset of Register
    //Not authenticated
    private final UserDataDataAccess userDataDataAccess;
    private final AuthDataDataAccess authDataDataAccess;

    public LoginService(UserDataDataAccess userDataDataAccess, AuthDataDataAccess authDataDataAccess){
        this.userDataDataAccess = userDataDataAccess;
        this.authDataDataAccess = authDataDataAccess;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        if(request.username() == null || request.password() == null){
            throw new IllegalArgumentException("Missing required parameter");
        }

        UserData user = userDataDataAccess.getUser(request.username());

        if (user == null || !user.password().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String authToken = authDataDataAccess.createAuth(user.username());
        return new LoginResult(user.username(), authToken);
    }

}
