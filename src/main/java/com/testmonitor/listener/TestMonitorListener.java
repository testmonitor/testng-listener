package com.testmonitor.listener;

import com.testmonitor.api.Client;
import com.testmonitor.listener.exceptions.MissingPropertiesFileException;
import com.testmonitor.listener.exceptions.MissingPropertyException;
import com.testmonitor.resources.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

/**
 * @author TestMonitor
 * TestMonitor TestNG listener.
 */
public class TestMonitorListener implements ISuiteListener, ITestListener {

    protected static TestMonitorTestRun testRun;

    /**
     * TestMonitorListener constructor
     * 
     * @throws NumberFormatException
     * @throws IOException
     * @throws URISyntaxException
     * @throws MissingPropertiesFileException
     * @throws MissingPropertyException
     */
    public TestMonitorListener() throws NumberFormatException, IOException, URISyntaxException, MissingPropertiesFileException, MissingPropertyException {
        TestMonitorListener.testRun = new TestMonitorTestRun(
            new Client(Configuration.getDomain(), Configuration.getToken()), 
            Configuration.getProjectId(), 
            Configuration.getMilestoneId(),
            Configuration.getTestRunPrefix()
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testSuite = this.generateTestSuiteName(result);
        String testCase = this.generateTestCaseName(result);

        TestResult testResult = new TestResult()
            .setTestResultCategoryId(TestResultCategory.PASSED)
                .setDescription("");

        try {
            testRun.storeTestResult(testSuite, testCase, testResult);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testSuite = this.generateTestSuiteName(result);
        String testCase = this.generateTestCaseName(result);

        File screenshot = this.generateScreenshot(result);

        TestResult testResult = new TestResult()
            .setTestResultCategoryId(TestResultCategory.FAILED)
            .setDescription(result.getThrowable().getMessage());

        if (screenshot != null) {
            testResult.addAttachment(screenshot);
        }

        try {
            testRun.storeTestResult(testSuite, testCase, testResult);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testSuite = this.generateTestSuiteName(result);
        String testCase = this.generateTestCaseName(result);

        TestResult testResult = new TestResult()
            .setTestResultCategoryId(TestResultCategory.CAUTION)
            .setDescription(result.getThrowable().getMessage());

        try {
            testRun.storeTestResult(testSuite, testCase, testResult);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param result Test result
     *
     * @return A test suite name
     */
    protected String generateTestSuiteName(ITestResult result) {
        return result.getTestContext().getSuite().getName();
    }

    /**
     * @param result Test result
     *
     * @return A test case name
     */
    protected String generateTestCaseName(ITestResult result) {
        return result.getName();
    }

    /**
     * @param result Test result
     *
     * @return The screenshot or null when there is no webdriver found
     */
    protected File generateScreenshot(ITestResult result) {
        try {
            Method method = result.getInstance().getClass().getMethod("getDriver");
            WebDriver webDriver = (WebDriver) method.invoke(result.getInstance());
            return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}