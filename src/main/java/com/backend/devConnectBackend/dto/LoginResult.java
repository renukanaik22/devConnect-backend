package com.backend.devConnectBackend.dto;

public sealed interface LoginResult
        permits LoginResult.Success,
        LoginResult.UserNotFound,
        LoginResult.InvalidPassword {

    record Success(String token) implements LoginResult {
    }

    record UserNotFound() implements LoginResult {
    }

    record InvalidPassword() implements LoginResult {
    }
}
