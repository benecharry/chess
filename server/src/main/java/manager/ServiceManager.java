package manager;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.GameDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import service.ClearApplicationService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

public class ServiceManager {
    private final UserDataMemoryDataAccess userDataDataAccess;
    private final AuthDataMemoryDataAccess authDataDataAccess;
    private final GameDataMemoryDataAccess gameDataDataAccess;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ClearApplicationService clearApplicationService;

    public ServiceManager() {
        this.userDataDataAccess = new UserDataMemoryDataAccess();
        this.authDataDataAccess = new AuthDataMemoryDataAccess();
        this.gameDataDataAccess = new GameDataMemoryDataAccess();
        this.registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        this.loginService = new LoginService(userDataDataAccess, authDataDataAccess);
        this.logoutService = new LogoutService(authDataDataAccess);
        this.clearApplicationService = new ClearApplicationService(userDataDataAccess, authDataDataAccess, gameDataDataAccess);
    }

    public RegisterService getRegisterService() {return registerService;}
    public LoginService getLoginService(){return loginService;};
    public LogoutService getLogoutService() {return logoutService;};
    public ClearApplicationService getClearApplicationService(){return clearApplicationService;}
}
