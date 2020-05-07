package net.zevrant.services.zevrant.oauth2.service.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidOTPException extends RuntimeException {

    public InvalidOTPException(String message) {
        super(message);
    }
}
