package chess;

public record UserData(String userName, String password, String email) {
    public  UserData newUser(String newUserName){
        return new UserData(userName, this.password, this.email);
    }
}