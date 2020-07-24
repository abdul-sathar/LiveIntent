package com.liveintent.helpers;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;
import com.amazonaws.services.kinesis.model.ShardIteratorType;
import static com.liveintent.tests.BaseTest.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class KinesisHelper {
	
	private AmazonKinesis client;
	
	public KinesisHelper(String accessKey, String secretKey, String serviceEndpoint, String region) {
		
		client=buildAmazonKinesis(accessKey, secretKey, serviceEndpoint, region);
	}
	
	/**
     * Get request
     *
     * @param routeURL Routing URL
     * @param seed Seed
     * @return Response
     */
	private Response getRequest(String routeURL, String seed)
	{
		System.out.println("Testing : "+routeURL+seed);
		return get(routeURL+seed);
	}
	
	/**
     * Verify the kinesis service request
     *
     * @param routeURL Routing URL
     * @param seed Seed
     * @return Response
     */
	public Response verifySericeIsUpAndRunning(String routeURL, String seed, String expectedStatusCode)
	{
		Response response = getRequest(routeURL,seed);
		try {
			Integer.parseInt(seed);
			response.then().assertThat().statusCode(Integer.parseInt(expectedStatusCode));
			verifyHeaderWithKey(response,HEADER_NAME);
		}catch(Exception e) {
			response.then().assertThat().statusCode(Integer.parseInt(expectedStatusCode)).and().header(HEADER_NAME, "");
		}
		
		return response;
	}
	
	/**
     * Verify the status code of the request
     *
     * @param expectedStatusCode Expected StatusCode
     * @param response Response
     */
	public void verifyStatusCode(String expectedStatusCode, Response response) {
		assertEquals(response.getStatusCode(), Integer.parseInt(expectedStatusCode));
	}

	/**
     * Verify the response header
     *
     * @param response Response
     * @param headerKey Hearder Key name
     */
	public void verifyHeaderWithKey(Response response, String headerKey) {
		assertTrue(response.getHeaders().hasHeaderWithName(headerKey));
		assertNotNull(response.getHeader(headerKey));
	}
	
	/**
     * Build Amazon Kinesis client
     *
     * @param accessKey AWS access Key
     * @param secretKey AWS secret Key
     * @param serviceEndpoint AWS kinesis endpoint url
     * @param region AWS region
     */
	public AmazonKinesis buildAmazonKinesis(String accessKey, String secretKey, String serviceEndpoint, String region) {
	    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
	    return AmazonKinesisClientBuilder.standard()
	      .withEndpointConfiguration(new EndpointConfiguration(serviceEndpoint, region))
	      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	      .build();
	}
	
	/**
     * Get shard Iterator
     *
     * @param streamName AWS data stream name
     * @param shardID AWS shard ID
     * @return GetShardIteratorResult
     */
	private GetShardIteratorResult getStreamShardIterator(String streamName, String shardID) {
	    GetShardIteratorRequest readShardsRequest = new GetShardIteratorRequest();
	    readShardsRequest.setStreamName(streamName);
	    readShardsRequest.setShardIteratorType(ShardIteratorType.LATEST);
	    readShardsRequest.setShardId(shardID);
	 
	    return client.getShardIterator(readShardsRequest);
	}
	
	/**
     * Get TransactionID from response
     *
     * @param response Response
     * @param headerName header key
     * @return TransactionID
     */
	private String getTransactionID(Response response, String headerName)
	{
		return response.headers().get(headerName).getValue();
	 
	}
	
	/**
     * Verify the record in correct data stream from response
     *
     * @param response Response
     * @param expectedStream expected Stream
     * @param seed Seed
     */
	public void verifyRecordPresentInCorrectStream(Response response, String expectedStream, String seed) {

		JSONObject oddRecord = getRecordData(STREAM_ODD, getShardID(STREAM_ODD));
		JSONObject evenRecord = getRecordData(STREAM_EVEN, getShardID(STREAM_EVEN));
		
		if (expectedStream.equalsIgnoreCase("even")) {
			assertEquals(seed, evenRecord.get("seed").toString());
			assertEquals(getTransactionID(response,HEADER_NAME), evenRecord.get("uuid").toString());
			assertNotEquals(getTransactionID(response,HEADER_NAME), oddRecord.get("uuid").toString());
		} else {
			assertEquals(seed, oddRecord.get("seed").toString());
			assertEquals(getTransactionID(response,HEADER_NAME), oddRecord.get("uuid").toString());
			assertNotEquals(getTransactionID(response,HEADER_NAME), evenRecord.get("uuid").toString());
		}
	}
	
	/**
     * Get record data from data stream
     *
     * @param streamName data stream Name
     * @param shardID Shard ID
     * @return record data
     */
	private JSONObject getRecordData(String streamName, String shardID) {

		GetShardIteratorResult getShardIteratorResult = getStreamShardIterator(streamName, shardID);
		GetRecordsRequest recordsRequest = new GetRecordsRequest();

		recordsRequest.setShardIterator(getShardIteratorResult.getShardIterator());
		recordsRequest.setLimit(300);

		GetRecordsResult getRecordsResult = client.getRecords(recordsRequest);

		List<Record> records = getRecordsResult.getRecords();
		try {
			int recordSize = records.size();
			return new JSONObject(records.get(recordSize - 1));
		} catch (IndexOutOfBoundsException ie) {
			return null;
		}
	}
	
	/**
     * Get Shard ID
     *
     * @param streamName data stream Name
     * @return Shard ID
     */
	private String getShardID(String streamName) {
		
		DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
		describeStreamRequest.setStreamName( streamName );
		List<Shard> shards = new ArrayList<>();
		String exclusiveStartShardId = null;
		do {
		    describeStreamRequest.setExclusiveStartShardId( exclusiveStartShardId );
		    DescribeStreamResult describeStreamResult = client.describeStream( describeStreamRequest );
		    shards.addAll( describeStreamResult.getStreamDescription().getShards() );
		    if (describeStreamResult.getStreamDescription().getHasMoreShards() && shards.size() > 0) {
		        exclusiveStartShardId = shards.get(shards.size() - 1).getShardId();
		    } else {
		        exclusiveStartShardId = null;
		    }
		} while ( exclusiveStartShardId != null );
		
		return exclusiveStartShardId;
	}
}
