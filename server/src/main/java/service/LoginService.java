package service;

import dataaccess.UserDataDataAccess;

public class LoginService {
    //Subset of Register
    //Not authenticated
    private final UserDataDataAccess userDataDataAccess;

    public LoginService(UserDataDataAccess userDataDataAccess) {
        this.userDataDataAccess = userDataDataAccess;
    }


}
