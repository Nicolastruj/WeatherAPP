package org.ulpgc.dacd.control;

public class MyStoreBuilderException extends Exception{
    public MyStoreBuilderException() {
        super();
    }

    public MyStoreBuilderException(String message) {
        super(message);
    }

    public MyStoreBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyStoreBuilderException(Throwable cause) {
        super(cause);
    }
}
