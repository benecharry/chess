package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class UserDataMemoryDataAccess implements UserDataDataAccess {
    //final private HashMap<>
    //Create user
    //Get user by name
    //Authenticate user
    //As for right now I am using the implementation of the petshop.
    private final int nextId = 1;
    private final HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if(users.containsKey(userData.username())){
            throw new DataAccessException("Username " + userData.username() + " already in use.");
        }
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
