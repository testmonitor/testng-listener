package com.testmonitor.listener.interfaces;

import org.openqa.selenium.WebDriver;

public interface HasWebdriver {

    /**
     * Returns your webdriver instance.
     *
     * @return Webdriver instance
     */
    public WebDriver getDriver();
}
