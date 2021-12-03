# TestMonitor TestNG Listener

This TestNG listener can be used to report the results for your test cases in [TestMonitor](https://www.testmonitor.com). Additionally, 
by implementing the `HasWebdriver` interface, Selenium test results will be annotated with a screenshot when test cases fail.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Changelog](#changelog)
- [Contributing](#contributing)
- [Credits](#credits)
- [License](#license)

## Installation

Before you start, make sure you have a [recent Java SDK](https://www.oracle.com/java/technologies/downloads/) installed.

### Maven Installation

We recommend installing the TestNG listener in your Java project using Maven. Add it as a dependency to your Maven configuration:

```xml
<dependency>
    <groupId>com.testmonitor</groupId>
    <artifactId>testng-listener</artifactId>
    <version>1.0</version>
</dependency>
```

### Manual Installation

If you prefer a standalone JAR library, checkout this repository and run Maven to compile the sources and generate a JAR file:

```sh
$ git checkout https://github.com/testmonitor/testng-listener.git
$ cd testng-listener
$ mvn package
```

Your JAR file will be available in the `target` directory.

## Usage

Start with adding the listener to listeners section in your test suite XML:

```xml
<suite name="Tests">
    <!-- ... -->
    <listeners>
        <listener class-name="com.testmonitor.listener.TestMonitorListener" />
    </listeners>
</suite>
```

Next, create a `testmonitor.properties` file in your project directory and add this configuration (change the values according to your preferences):

```properties
TESTMONITOR_DOMAIN=mydomain.testmonitor.com
TESTMONITOR_TOKEN=itsatoken
TESTMONITOR_PROJECT_ID=1
TESTMONITOR_MILESTONE_ID=1
TESTMONITOR_TEST_RUN_PREFIX=AT 
```

In case you are using the Selenium framework, you can optionally implement the `HasWebdriver` in your test cases. This exposes the web driver to the listener, allowing it to take a screenshot if your test case has failed. 

Here is an example:

```java
public class MyTestCase implements HasWebdriver {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    private void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        this.driver = new ChromeDriver();
    }

    @Test
    public void myTest() {
        this.driver.get("https://www.testmonitor.com/");
    }

    public WebDriver getDriver() {
        return this.driver;
    }
}
```

## Contributing

Refer to [CONTRIBUTING](CONTRIBUTING.md) for contributing details.

## Credits

* **Thijs Kok** - *Lead developer* - [ThijsKok](https://github.com/thijskok)
* **Stephan Grootveld** - *Developer* - [Stefanius](https://github.com/stefanius)
* **Muriel Nooder** - *Developer* - [ThaNoodle](https://github.com/thanoodle)

## License

The MIT License (MIT). Refer to the [License](LICENSE.md) for more information.
