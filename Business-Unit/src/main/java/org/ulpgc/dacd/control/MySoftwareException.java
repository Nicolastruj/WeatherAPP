package org.ulpgc.dacd.control;

public class MySoftwareException extends Exception{
    public MySoftwareException() {
        super();
    }

    public MySoftwareException(String message) {
        super(message);
    }

    public MySoftwareException(String message, Throwable cause) {
        super(message, cause);
    }

    public MySoftwareException(Throwable cause) {
        super(cause);
    }
}
