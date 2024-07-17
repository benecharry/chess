package manager;

import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import service.RegisterService;

public class ServiceManager {
    private final UserDataMemoryDataAccess userDataDataAccess;
    private final AuthDataMemoryDataAccess authDataDataAccess;
    private final RegisterService registerService;

    public ServiceManager() {
        this.userDataDataAccess = new UserDataMemoryDataAccess();
        this.authDataDataAccess = new AuthDataMemoryDataAccess();
        this.registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
    }

    public RegisterService getRegisterService() {
        return registerService;
    }
}
