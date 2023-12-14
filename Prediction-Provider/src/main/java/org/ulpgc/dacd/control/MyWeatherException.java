package org.ulpgc.dacd.control;

public class MyWeatherException extends Exception{
    public MyWeatherException() {
        super();
    }

    public MyWeatherException(String message) {
        super(message);
    }

    public MyWeatherException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyWeatherException(Throwable cause) {
        super(cause);
    }
}
