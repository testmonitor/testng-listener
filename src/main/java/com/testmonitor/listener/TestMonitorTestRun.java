package com.testmonitor.listener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.testmonitor.api.Client;
import com.testmonitor.resources.Milestone;
import com.testmonitor.resources.Project;
import com.testmonitor.resources.TestCase;
import com.testmonitor.resources.TestCaseFolder;
import com.testmonitor.resources.TestResult;
import com.testmonitor.resources.TestRun;

/**
 * @author TestMonitor
 * TestMonitor test run.
 */
public class TestMonitorTestRun {

    private Client client;

    private Project project;

    private Milestone milestone;

    private TestRun testRun;

    private TestCaseFolder parentTestCaseFolder;

    /**
     * TestMonitorTestRun constructor
     *
     * @param client TestMonitor client
     * @param projectId Project ID
     * @param milestoneId Milestone ID
     * @param testRunPrefix Test run prefix
     * @param parentTestCaseFolder Parent test case folder
     * @throws IOException
     * @throws URISyntaxException
     */
    public TestMonitorTestRun(Client client, Integer projectId, Integer milestoneId, String testRunPrefix, TestCaseFolder parentTestCaseFolder) throws IOException, URISyntaxException {
        this.client = client;
        this.parentTestCaseFolder = parentTestCaseFolder;

        this.initializeTestRun(projectId, milestoneId, testRunPrefix);
    }

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

        // Retrieve project and milestone
        this.project = this.client.projects().get(projectId);
        this.milestone = this.client.milestones(project).get(milestoneId);

        // Create a new test run
        this.testRun = this.client.testRuns(this.project).findOrCreate(this.generateTestRunName(testRunPrefix), milestone.getId());

        // Self-assign test run
        this.client.testRuns(this.project).assignUsers(
            testRun,
            new ArrayList<>(Arrays.asList(this.client.users().authenticatedUser().getId()))
        );
    }

    /**
     * Generates a test run name using a timestamp.
     *
     * @param prefix Test run prefix
     * @return A test run name
     */
    private String generateTestRunName(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return prefix + timestamp;
    }

    /**
     * Create or re-use a test case, assign it to this run, and store a test result.
     *
     * @param testCaseFolderName Test suite name
     * @param testCaseName Test case name
     * @param testResult Test result object
     * @throws URISyntaxException
     * @throws IOException
     */
    public TestResult storeTestResult(String testCaseFolderName, String testCaseName, TestResult testResult) throws IOException, URISyntaxException {
        if (this.parentTestCaseFolder instanceof TestCaseFolder) {
            return this.storeTestResult(testCaseFolderName, testCaseName, this.parentTestCaseFolder, testResult);
        }

        // Find or create a matching test suite and test case
        TestCaseFolder folder = this.client.testCaseFolders(this.project).findOrCreate(testCaseFolderName);
        TestCase testCase = this.client.testCases(this.project).findOrCreate(testCaseName, folder);

        return this.storeTestResult(testCase, testResult);
    }

    /**
     * Create or re-use a test case, assign it to this run, and store a test result.
     *
     * @param testCaseFolderName Test suite name
     * @param testCaseName Test case name
     * @param parentFolder Parent folder
     * @param testResult Test result object
     * @throws URISyntaxException
     * @throws IOException
     */
    public TestResult storeTestResult(String testCaseFolderName, String testCaseName, TestCaseFolder parentFolder, TestResult testResult) throws IOException, URISyntaxException {
        // Find or create a matching test suite and test case
        TestCaseFolder folder = this.client.testCaseFolders(this.project).findOrCreate(testCaseFolderName, parentFolder);
        TestCase testCase = this.client.testCases(this.project).findOrCreate(testCaseName, folder);

        return this.storeTestResult(testCase, testResult);
    }

    /**
     * Create or re-use a test case, assign it to this run, and store a test result.
     *
     * @param testCase Test case object
     * @param testResult Test result object
     * @throws URISyntaxException
     * @throws IOException
     */
    public TestResult storeTestResult(TestCase testCase, TestResult testResult) throws IOException, URISyntaxException {
        // Assign test case to test run
        this.client.testRuns(this.project).mergeTestCases(this.testRun, List.of(testCase.getId()));

        // Add test case and run data to test result
        testResult.setTestCaseId(testCase.getId())
                  .setTestRunId(this.testRun.getId());

        return this.client.testResults(this.project).create(testResult);
    }
}
