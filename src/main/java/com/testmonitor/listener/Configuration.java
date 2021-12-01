package com.testmonitor.listener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.testmonitor.listener.exceptions.MissingPropertiesFileException;
import com.testmonitor.listener.exceptions.MissingPropertyException;

/**
 * @author TestMonitor
 * Configuration class for TestMonitor TestNG listener.
 */
public class Configuration {
    final static String propertiesFile = "testmonitor.properties";

    final static String domainProperty = "TESTMONITOR_DOMAIN";

    final static String tokenProperty = "TESTMONITOR_TOKEN";

    final static String projectIdProperty = "TESTMONITOR_PROJECT_ID";

    final static String milestoneIdProperty = "TESTMONITOR_MILESTONE_ID";

    final static String testRunPrefixProperty = "TESTMONITOR_TEST_RUN_PREFIX";

    private static Configuration instance;

    private Properties properties;

    /**
     * Configuration constructor
     * 
     * @throws MissingPropertiesFileException
     */
    private Configuration() throws MissingPropertiesFileException {
        this.initializeProperties();
    }

    /**
     * Loads the properties file.
     *
     * @throws MissingPropertiesFileException
     */
    private void initializeProperties() throws MissingPropertiesFileException {
        this.properties = new Properties();

        try {
            FileInputStream in = new FileInputStream(Configuration.propertiesFile);
            this.properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new MissingPropertiesFileException();
        }
    }
    
    /**
     * @return Configuration instance
     *
     * @throws MissingPropertiesFileException
     */
    private static Configuration getInstance() throws MissingPropertiesFileException {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    /**
     * @return TestMonitor domain
     * 
     * @throws MissingPropertiesFileException
     * @throws MissingPropertyException
     */
    public static String getDomain() throws MissingPropertiesFileException, MissingPropertyException {
        return getInstance().getPropertyValue(Configuration.domainProperty);
    }

    /**
     * @return Auth token
     * 
     * @throws MissingPropertiesFileException
     * @throws MissingPropertyException
     */
    public static String getToken() throws MissingPropertiesFileException, MissingPropertyException {
        return getInstance().getPropertyValue(Configuration.tokenProperty);
    }
    
    /**
     * @return Project ID
    
     * @throws MissingPropertiesFileException
     * @throws NumberFormatException
     * @throws MissingPropertyException
     */
    public static Integer getProjectId() throws MissingPropertiesFileException, NumberFormatException, MissingPropertyException {
        return Integer.parseInt(getInstance().getPropertyValue(Configuration.projectIdProperty));
    }
    
    /**
     * @return Milestone ID
    
     * @throws MissingPropertiesFileException
     * @throws NumberFormatException
     * @throws MissingPropertyException
     */
    public static Integer getMilestoneId() throws MissingPropertiesFileException, NumberFormatException, MissingPropertyException {
        return Integer.parseInt(getInstance().getPropertyValue(Configuration.milestoneIdProperty));
    }
    
    /**
     * @return Test run prefix
     
     * @throws MissingPropertiesFileException
     * @throws MissingPropertyException
     */
    public static String getTestRunPrefix() throws MissingPropertiesFileException, MissingPropertyException {
        return getInstance().getPropertyValue(Configuration.testRunPrefixProperty);
    }
    
    /**
     * @param property the property you want to use
     *
     * @return The value of the property
     * @throws MissingPropertyException
     */
    public String getPropertyValue(String property) throws MissingPropertyException {
        if (!this.properties.containsKey(property)) {
            throw new MissingPropertyException(property);
        }

        return this.properties.getProperty(property);
    }
}
