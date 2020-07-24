package com.liveintent.tests;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.liveintent.helpers.Environment;
import com.liveintent.helpers.KinesisHelper;
import static com.liveintent.helpers.PropertyUtils.*;

import java.lang.reflect.Method;


public class BaseTest {
	protected String ROUTING_URL;
	protected String KINESIS_ENDPOINT_URL;
	protected String AWS_ACCESS_KEY_ID;
	protected String AWS_SECRET_ACCESS_KEY;
	protected String REGION_NAME;
	protected KinesisHelper kinesis;
	public static String STREAM_ODD;
	public static String STREAM_EVEN;
	public static String HEADER_NAME;
	protected ExtentSparkReporter spark;
	protected ExtentReports extent;
	public ExtentTest test;
	
	
	@BeforeSuite(alwaysRun = true)
	protected void SuiteLevelSetup() {
		ROUTING_URL = get(Environment.ROUTING_URL);
		KINESIS_ENDPOINT_URL = get(Environment.KINESIS_ENDPOINT_URL);
		AWS_ACCESS_KEY_ID = get(Environment.AWS_ACCESS_KEY_ID);
		AWS_SECRET_ACCESS_KEY = get(Environment.AWS_SECRET_ACCESS_KEY);
		REGION_NAME = get(Environment.REGION_NAME);
		STREAM_EVEN = get(Environment.STREAM_EVEN);
		STREAM_ODD = get(Environment.STREAM_ODD);
		HEADER_NAME = get(Environment.HEADER_NAME);
		kinesis = new KinesisHelper(AWS_ACCESS_KEY_ID,AWS_SECRET_ACCESS_KEY,KINESIS_ENDPOINT_URL, REGION_NAME);
	}
	
	@BeforeTest
    public void baseBeforeTest() {

        spark = new ExtentSparkReporter(System.getProperty("user.dir") + "/TestReport/testReport.html");
        spark.loadConfig("src/test/resources/html-config.xml");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("User ", System.getProperty("user.name"));
    }

    @AfterTest
    public void tearDown() {
        extent.flush();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void logStartMethod(Method testMethod) {
    	
        test = extent.createTest(testMethod.getName(), "Executed "+testMethod.getName());
    }
    
    @AfterMethod
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " FAILED ", ExtentColor.RED));
            test.fail(result.getThrowable());

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " PASSED ", ExtentColor.GREEN));
        } else {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " SKIPPED ", ExtentColor.ORANGE));
            test.skip(result.getThrowable());
        }
    }


}
