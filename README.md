# Liveintent QA 
API Test Automation framework designed specifically for [LiveIntent](https://www.liveintent.com/) comapy.

## Summary

Framework is build using the stack
* [REST-assured](http://rest-assured.io/)
* [Java 1.8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [TestNG](https://testng.org/doc/)
* [Extent Reports](https://extentreports.com/)

--------------------------------------------------------------------------------------------
Getting the code:
--------------------------------------------------------------------------------------------
`git clone https://github.com/abdul-sathar/LiveIntent.git` (Clone the test suite)

--------------------------------------------------------------------------------------------
Test run configuration:
--------------------------------------------------------------------------------------------
***config.properties***
<br>
It is possible to configure test run via **config.properties**

- route_url = http://localhost:9000/route/
- kinesis_endpoint_url = http://localhost:4568/
- aws_access_key_id = none
- aws_secret_access_key = none
- region_name = none
- stream_odd = li-stream-odd
- stream_even = li-stream-even
- header_name = X-Transaction-Id

--------------------------------------------------------------------------------------------
Running the test suite:
--------------------------------------------------------------------------------------------
#### Required software to run tests from Command Line
* [Java 1.8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [Apache Maven 3](http://maven.apache.org/download.cgi)

## Steps to run tests:
***Pre-condition:*** make sure the service under test is up and running in the localhost

1. Clone the repository.

2. Navigate to ***'Liveintent'*** folder by `cd Liveintent` 

3. Execute the tests by `mvn clean test` 

#### Extent report
You can find generated extent report for tests under ***"/TestReport/testReporter.html"***

### Contact
* abdul.sathar2612@gmail.com