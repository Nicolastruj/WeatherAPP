package org.ulpgc.dacd.control;

public class MyHotelException extends Exception {
    public MyHotelException(String message) {
        super(message);
    }
    public MyHotelException(String message, Throwable cause) {
        super(message, cause);
    }
}