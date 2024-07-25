package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
public class UserDataMemoryDataAccess implements UserDataDataAccess {
    //final private HashMap<>
    //Create user
    //Get user by name
    //Authenticate user
    //As for right now I am using the implementation of the petshop.
    private final int nextId = 1;
    //TO DO: ID is never used. FOR LATER.
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
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("User with username not found.");
        }
        return user;
    }
    @Override
    public void clear(){
        users.clear();
    }

    @Override
    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        UserData user = getUser(username);
        return user != null && user.password().equals(providedClearTextPassword);
    }
}