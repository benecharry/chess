package model;

public record UserData(String username, String password, String email) {
    public  UserData newUser(String newUsername){
        return new UserData(username, this.password, this.email);
    }
}