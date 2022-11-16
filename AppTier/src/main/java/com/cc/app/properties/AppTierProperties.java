package com.cc.app.properties;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;

@Getter
public class AppTierProperties {

    private final String REQUEST_SQS = "https://sqs.us-east-1.amazonaws.com/867386276772/openstack-request-sqs";
    private final String RESPONSE_SQS = "https://sqs.us-east-1.amazonaws.com/867386276772/openstack-response-sqs";

    private final String REQUEST_S3 = "openstack-546-request-bucket";
    private final String RESPONSE_S3 = "openstack-546-response-bucket";

    public BasicAWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                "",
                ""
        );
    }
}