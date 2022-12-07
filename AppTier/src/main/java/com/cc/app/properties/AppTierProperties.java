package com.cc.app.properties;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;

@Getter
public class AppTierProperties {

    private final String REQUEST_SQS = "https://sqs.us-east-1.amazonaws.com/501410091785/request";
    private final String RESPONSE_SQS = "https://sqs.us-east-1.amazonaws.com/501410091785/response";

    private final String REQUEST_S3 = "openstack-request";
    private final String RESPONSE_S3 = "openstack-response";

    public BasicAWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                "",
                ""
        );
    }
}