package org.julleon.manager.client.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException{

    private final List<String> errorMessages;

    public BadRequestException(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public BadRequestException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public BadRequestException(String message, Throwable cause, List<String> errorMessages) {
        super(message, cause);
        this.errorMessages = errorMessages;
    }

    public BadRequestException(Throwable cause, List<String> errorMessages) {
        super(cause);
        this.errorMessages = errorMessages;
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<String> errorMessages) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMessages = errorMessages;
    }
}
