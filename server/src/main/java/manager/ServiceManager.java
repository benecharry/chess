package manager;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.GameDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import service.ClearApplicationService;
import service.RegisterService;

public class ServiceManager {
    private final UserDataMemoryDataAccess userDataDataAccess;
    private final AuthDataMemoryDataAccess authDataDataAccess;
    private final GameDataMemoryDataAccess gameDataDataAccess;
    private final RegisterService registerService;
    private final ClearApplicationService clearApplicationService;

    public ServiceManager() {
        this.userDataDataAccess = new UserDataMemoryDataAccess();
        this.authDataDataAccess = new AuthDataMemoryDataAccess();
        this.gameDataDataAccess = new GameDataMemoryDataAccess();
        this.registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        this.clearApplicationService = new ClearApplicationService(userDataDataAccess, authDataDataAccess, gameDataDataAccess);
    }

    public RegisterService getRegisterService() {
        return registerService;
    }

    public ClearApplicationService getClearApplicationService(){
        return clearApplicationService;
    }
}
