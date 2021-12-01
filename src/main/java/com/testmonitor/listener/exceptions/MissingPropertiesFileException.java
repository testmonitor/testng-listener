package com.testmonitor.listener.exceptions;

public class MissingPropertiesFileException extends Exception {

    /**
     * MissingPropertiesFileException constructor
     */
    public MissingPropertiesFileException() {
        super("Could not find the testmonitor.properties file.");
    }
}
