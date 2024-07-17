package service;

import dataaccess.AuthDataDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataDataAccess;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterService {
    //TO-DO
    //Validate input parameters
    //Validate the username is unique and no taken
    //Create a new user object and pass the data to the access class to save in the database
    //Log in
    //Generate authToken
    //Not authenticated with authToken! 43!
    private final UserDataDataAccess userDataDataAccess;
    private final AuthDataDataAccess authDataDataAccess;

    public RegisterService(UserDataDataAccess userDataDataAccess, AuthDataDataAccess authDataDataAccess) {
        this.userDataDataAccess = userDataDataAccess;
        this.authDataDataAccess = authDataDataAccess;
    }

    RegisterResult register(RegisterRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null || request.email() == null){
            //I was not sure to use this or to use the DataAccessException
            throw new IllegalArgumentException("Missing required parameter");
        }

        if(userDataDataAccess.getUser(request.username()) != null){
            throw new IllegalArgumentException("Username is already taken.");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDataDataAccess.createUser(newUser);

        String authToken = authDataDataAccess.createAuth(newUser.username());

        return new RegisterResult(newUser.username(), authToken);
        //Register handler will process incoming register requests. HTTP request, unpacked and then the handler will
        //call the register method on user server. Register method send the result to the handler.
    }
}