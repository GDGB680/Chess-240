package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegisterRequest request = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            // Execute
            RegisterResult result = userService.register(request);

            // Verify
            assertNotNull(result);
            assertEquals("username1", result.username());
            assertNotNull(result.authToken());
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Register Failure - Already Taken")
    public void registerFailureAlreadyTaken() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegisterRequest request1 = new RegisterRequest("username1", "password1", "email1@example.com");
        RegisterRequest request2 = new RegisterRequest("username1", "password2", "email2@example.com");

        try {
            userService.register(request1);

            // Execute & Verify
            DataAccessException exception = assertThrows(DataAccessException.class,
                    () -> userService.register(request2));
            assertEquals("already taken", exception.getMessage());
        } catch (DataAccessException e) {
            fail("First registration should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            userService.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest("username1", "password1");

            // Execute
            LoginResult result = userService.login(loginRequest);

            // Verify
            assertNotNull(result);
            assertEquals("username1", result.username());
            assertNotNull(result.authToken());
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Login Failure - Invalid Credentials")
    public void loginFailureInvalidCredentials() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            userService.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest("username1", "wrongpassword");

            // Execute & Verify
            DataAccessException exception = assertThrows(DataAccessException.class,
                    () -> userService.login(loginRequest));
            assertEquals("unauthorized", exception.getMessage());
        } catch (DataAccessException e) {
            fail("Registration should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());

            // Execute & Verify (no exception means success)
            assertDoesNotThrow(() -> userService.logout(logoutRequest));
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Logout Failure - Invalid Token")
    public void logoutFailureInvalidToken() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        LogoutRequest logoutRequest = new LogoutRequest("invalid-token");

        // Execute & Verify
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> userService.logout(logoutRequest));
        assertEquals("unauthorized", exception.getMessage());
    }
}
