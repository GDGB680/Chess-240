package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("bad request");
        }
        if (dataAccess.getUser(request.username()) != null) {
            throw new DataAccessException("already taken");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(newUser);
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.createAuthToken(authData);
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.getPassword().equals(request.password())) {
            throw new DataAccessException("unauthorized");
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.createAuthToken(authData);
        return new LoginResult(request.username(), authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        if (request.authToken() == null) {
            throw new DataAccessException("bad request");
        }
        AuthData authData = dataAccess.getAuthToken(request.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        dataAccess.deleteAuthToken(request.authToken());
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
