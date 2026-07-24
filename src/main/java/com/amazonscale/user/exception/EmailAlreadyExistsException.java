package com.amazonscale.user.exception;

import lombok.Builder;

@Builder
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists.");
    }

}