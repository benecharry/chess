package service;

import dataaccess.DataAccessException;
import dataaccess.UserDataDataAccess;
import request.RegisterRequest;
import result.RegisterResult;

public class Register {
    //TO-DO
    //Validate input parameters
    //Validate the username is unique and no taken
    //Create a new user object and pass the data to the access class to save in the database
    //Log in
    //Generate authToken
    //Not authenticated
    private final UserDataDataAccess userDataDataAccess;

    public Register(UserDataDataAccess userDataDataAccess) {
        this.userDataDataAccess = userDataDataAccess;
    }

    RegisterResult register(RegisterRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null || request.email() == null){
            //I was not sure to use this or to use the DataAccessException
            throw new IllegalArgumentException("Missing required parameter");
        }

        return null;
    }
}