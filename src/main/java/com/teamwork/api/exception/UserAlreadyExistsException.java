package com.teamwork.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при попытке создать пользователя,
 * который уже существует (с таким же username или email).
 */
@ResponseStatus(HttpStatus.CONFLICT) // Эта аннотация автоматически вернет клиенту статус 409
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
