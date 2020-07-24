package com.liveintent.helpers;

public enum Environment {
	ROUTING_URL("route_url"),
	KINESIS_ENDPOINT_URL("kinesis_endpoint_url"),
	AWS_ACCESS_KEY_ID("aws_access_key_id"),
	AWS_SECRET_ACCESS_KEY("aws_secret_access_key"),
	REGION_NAME("region_name"),
	STREAM_ODD("stream_odd"),
	STREAM_EVEN("stream_even"),
	HEADER_NAME("header_name");

    private final String key;

    Environment(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
