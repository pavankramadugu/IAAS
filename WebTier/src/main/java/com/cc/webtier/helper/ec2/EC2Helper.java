package com.cc.webtier.helper.ec2;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.cc.webtier.properties.WebTierProperties;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
@Slf4j
public class EC2Helper {

    private final WebTierProperties webTierProperties;

    public EC2Helper(WebTierProperties webTierProperties) {
        this.webTierProperties = webTierProperties;
    }

    public Integer getInstancesCount() {
        log.info("Getting Running Instances Count");
        var runningInstancesFilter = new Filter();
        runningInstancesFilter.setName("instance-state-name");
        runningInstancesFilter.setValues(Arrays.asList("running", "pending"));
        var describeInstancesRequest = new DescribeInstancesRequest();
        describeInstancesRequest.setFilters(Collections.singletonList(runningInstancesFilter));
        describeInstancesRequest.setMaxResults(1000);

        var count = new AtomicInteger();
        var amazonEC2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                .withRegion(Regions.US_EAST_1).build();
        amazonEC2.describeInstances(describeInstancesRequest)
                .getReservations()
                .forEach(reservation -> count.addAndGet(reservation.getInstances().size()));
        amazonEC2.shutdown();
        return count.get();
    }

    public void createInstance(Integer instanceCount) {
        IntStream.range(0, instanceCount).forEach(i -> {

            var date = LocalDateTime.now();
            String instanceId = String.valueOf(date.getHour()) + String.valueOf(date.getMinute())
                    + String.valueOf(date.getSecond());
            Try.run(() -> {
                log.info("Starting EC2 Instance with ID: " + instanceId);
                startTheInstance(webTierProperties.getAppTierImageId(),
                        webTierProperties.getAwsKeyPair(), "AppTier-" + instanceId);
            }).onFailure(ex -> log.error("Failed to Create Instance " + instanceId + " " + ex.getMessage()));
        });
    }

    private void startTheInstance(String amiId,
                                  String keyName,
                                  String instanceName) throws IOException {

        var sourceStream = getClass().getResourceAsStream("/userData.txt");
        assert sourceStream != null;
        byte[] sourceBytes = IOUtils.toByteArray(sourceStream);
        String encodedUserData = Base64.getEncoder().encodeToString(sourceBytes);

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(amiId)
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds(webTierProperties.getSecurityGroup())
                .withKeyName(keyName)
                .withUserData(encodedUserData)
                .withTagSpecifications(new TagSpecification().withTags(new Tag().withKey("Name")
                        .withValue(instanceName)).withResourceType("instance"))
                .withInstanceInitiatedShutdownBehavior(ShutdownBehavior.Terminate);

        Try.run(() -> {
            var amazonEC2 = AmazonEC2ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                    .withRegion(Regions.US_EAST_1).build();
            amazonEC2.runInstances(runInstancesRequest);
            sourceStream.close();
            log.info("Started EC2 Instance with Name: " + instanceName);
            amazonEC2.shutdown();
        });
    }
}
