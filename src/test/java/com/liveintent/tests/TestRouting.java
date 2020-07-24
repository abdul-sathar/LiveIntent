package com.liveintent.tests;

import org.testng.annotations.Test;

import com.liveintent.helpers.TestDataProvider;

import io.restassured.response.Response;

public class TestRouting extends BaseTest {

	@Test(dataProvider = "validData", dataProviderClass = TestDataProvider.class)
	public void testRoutingWithValidSeed(String seed,String expectedStatusCode, String expectedStream)
	{
		Response response = kinesis.verifySericeIsUpAndRunning(ROUTING_URL, seed,expectedStatusCode);
		kinesis.verifyRecordPresentInCorrectStream(response, expectedStream, seed);
		test.pass("Kinesis service with valid seed :"+seed+" is running successfully with status code :"+response.getStatusCode());
		test.pass("records are present in correct stream :"+expectedStream);
	}
	
	@Test(dataProvider = "invalidData", dataProviderClass = TestDataProvider.class)
	public void testRoutingWithInvalidSeed(String seed,String expectedStatusCode)
	{
		kinesis.verifySericeIsUpAndRunning(ROUTING_URL, seed, expectedStatusCode);
		test.pass("Tested routing with invalid seed :"+seed);
	}
}
