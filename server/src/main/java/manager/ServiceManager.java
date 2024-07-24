package manager;

import dataaccess.*;
import service.*;

public class ServiceManager {
    private final UserDataDataAccess userDataDataAccess;
    private final AuthDataDataAccess authDataDataAccess;
    private final GameDataDataAccess gameDataDataAccess;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListGamesService listGamesService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;
    private final ClearApplicationService clearApplicationService;

    public ServiceManager(UserDataDataAccess userDataDataAccess, AuthDataDataAccess authDataDataAccess, GameDataDataAccess gameDataDataAccess) {
        this.userDataDataAccess = userDataDataAccess;
        this.authDataDataAccess = authDataDataAccess;
        this.gameDataDataAccess = gameDataDataAccess;
        this.registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        this.loginService = new LoginService(userDataDataAccess, authDataDataAccess);
        this.logoutService = new LogoutService(authDataDataAccess);
        this.listGamesService = new ListGamesService(authDataDataAccess, gameDataDataAccess);
        this.createGameService = new CreateGameService(authDataDataAccess, gameDataDataAccess);
        this.joinGameService = new JoinGameService(authDataDataAccess, gameDataDataAccess);
        this.clearApplicationService = new ClearApplicationService(userDataDataAccess, authDataDataAccess, gameDataDataAccess);
    }

    public ServiceManager() {
        this(new UserDataMemoryDataAccess(), new AuthDataMemoryDataAccess(), new GameDataMemoryDataAccess());
    }

    public RegisterService getRegisterService() {return registerService;}
    public LoginService getLoginService(){return loginService;};
    public LogoutService getLogoutService() {return logoutService;};
    public ListGamesService getListGamesService(){return listGamesService;};
    public CreateGameService getCreateGameService(){return createGameService;};
    public JoinGameService getJoinGameService(){return joinGameService;};
    public ClearApplicationService getClearApplicationService(){return clearApplicationService;}
}