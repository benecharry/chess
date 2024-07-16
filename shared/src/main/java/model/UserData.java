package model;

public record UserData(String username, String password, String email) {
    //Maybe won't need it.
    public  UserData newUser(String newUsername){
        return new UserData(username, this.password, this.email);
    }
}