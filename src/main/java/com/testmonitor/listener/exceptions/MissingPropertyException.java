package com.testmonitor.listener.exceptions;

public class MissingPropertyException extends Exception {

    /**
     * MissingPropertyException constructor
     *
     * @param property Missing property
     */
    public MissingPropertyException(String property) {
        super("Property is missing: " + property);
    }
}
