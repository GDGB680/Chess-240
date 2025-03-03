package service;

import chess.*;
import dataaccess.*;
import model.*;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // 1. Validate the request (e.g., check for nulls, empty strings)
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("bad request"); // Map this to a 400 error
        }

        // 2. Check if the username is already taken (using your DAO)
        if (dataAccess.getUser(request.username()) != null) {
            throw new DataAccessException("already taken"); // Map this to a 403 error
        }

        // 3. Create the user (using your DAO)
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(newUser);

        // 4. Generate an auth token
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.createAuthToken(authData);

        // 5. Return the successful result
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        // 1. Validate the request
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("bad request"); // Map this to a 400 error
        }

        // 2. Retrieve the user from the database
        UserData user = dataAccess.getUser(request.username());

        // 3. Check if the user exists and the password matches
        if (user == null || !user.getPassword().equals(request.password())) {
            throw new DataAccessException("unauthorized"); // Map this to a 401 error
        }

        // 4. Generate a new auth token
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.createAuthToken(authData);

        // 5. Return the login result
        return new LoginResult(request.username(), authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        // 1. Validate the request
        if (request.authToken() == null) {
            throw new DataAccessException("bad request"); // Map this to a 400 error
        }

        // 2. Verify that the authToken exists (is authorized)
        AuthData authData = dataAccess.getAuthToken(request.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized"); // Map this to a 401 error
        }

        // 3. Delete the auth token
        dataAccess.deleteAuthToken(request.authToken());
    }

    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
