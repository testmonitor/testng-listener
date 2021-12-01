package com.testmonitor.listener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import com.testmonitor.api.Client;
import com.testmonitor.resources.Milestone;
import com.testmonitor.resources.Project;
import com.testmonitor.resources.TestCase;
import com.testmonitor.resources.TestResult;
import com.testmonitor.resources.TestRun;
import com.testmonitor.resources.TestSuite;
import com.testmonitor.resources.User;

/**
 * @author TestMonitor
 * TestMonitor test run.
 */
public class TestMonitorTestRun {

    private Client client;

    private Project project;

    private Milestone milestone;

    private TestRun testRun;

    private User user;

    /**
     * TestMonitorTestRun constructor
     *
     * @param client TestMonitor client
     * @param projectId Project ID
     * @param milestoneId Milestone ID
     * @param testRunPrefix Test run prefix
     * @throws IOException
     * @throws URISyntaxException
     */
    public TestMonitorTestRun(Client client, Integer projectId, Integer milestoneId, String testRunPrefix) throws IOException, URISyntaxException {
        this.client = client;

        this.initializeTestRun(projectId, milestoneId, testRunPrefix);
    }

    /**
     * Creates a new test run.
     *
     * @param projectId Project ID
     * @param milestoneId Milestone ID
     * @param testRunPrefix Test run prefix
     * @throws IOException
     * @throws URISyntaxException
     */
    protected void initializeTestRun(Integer projectId, Integer milestoneId, String testRunPrefix) throws IOException, URISyntaxException {
        this.project = this.client.projects().get(projectId);
        this.milestone = this.client.milestones(project).get(milestoneId);

        this.testRun = this.client.testRuns(this.project).findOrCreate(this.generateTestRunName(testRunPrefix), milestone.getId());
        this.user = this.client.users().authenticatedUser();

        this.client.testRuns(this.project).assignUsers(
            testRun, 
            new ArrayList<>(Arrays.asList(user.getId()))
        );
    }
    
    /**
     * @param prefix Test run prefix
     * @return A test run name
     */
    private String generateTestRunName(String prefix) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return prefix + timestamp;
    }

    /**
     * Create or re-use a test case, assign it to this run, and store a test result.
     *
     * @param testSuiteName Test suite name
     * @param testCaseName Test case name
     * @param testResult Test result object
     * @throws URISyntaxException
     * @throws IOException
     */
    public TestResult storeTestResult(String testSuiteName, String testCaseName, TestResult testResult) throws IOException, URISyntaxException {
        TestSuite testSuite = this.client.testSuites(this.project).findOrCreate(testSuiteName);
        TestCase testCase = this.client.testCases(this.project).findOrCreate(testCaseName, testSuite.getId());

        testResult.setTestCaseId(testCase.getId())
                  .setTestRunId(this.testRun.getId());

        return this.client.testResults(this.project).create(testResult);
    }

    /**
     * Create or re-use a test case, assign it to this run, and store a test result.
     *
     * @param testSuiteName Test suite name
     * @param testCaseName Test case name
     * @param testResult Test result object
     * @param attachment File attachment
     * @throws URISyntaxException
     * @throws IOException
     */
    public TestResult storeTestResult(String testSuiteName, String testCaseName, TestResult testResult, File attachment) throws IOException, URISyntaxException {
        TestResult result = this.storeTestResult(testSuiteName, testCaseName, testResult);

        if (attachment != null) {
            this.client.testResults(this.project).addAttachment(result, attachment);
        }

        return result;
    }
}
