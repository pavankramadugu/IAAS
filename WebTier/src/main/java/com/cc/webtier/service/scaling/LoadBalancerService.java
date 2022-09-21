package com.cc.webtier.service.scaling;

import com.cc.webtier.helper.ec2.EC2Helper;
import com.cc.webtier.helper.sqs.SQSHelper;
import com.cc.webtier.properties.WebTierProperties;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadBalancerService {

    private final EC2Helper ec2Helper;

    private final SQSHelper sqsHelper;

    private final WebTierProperties webTierProperties;

    public void scale() {
        var oldQueueSize = new AtomicInteger(0);
        while(true) {
            Try.run(() -> {
                var runningInstanceCount = ec2Helper.getInstancesCount();
                log.info("Number of Running Instances: " + runningInstanceCount);

                sqsHelper.getSQSMessageCount().ifPresent(count -> {
                    var messageCount = Integer.parseInt(count);
                    log.info("Number of Messages in the Queue: " + messageCount);
                    var delta =  messageCount - oldQueueSize.get();
                    log.info("Message Delta: " + delta);
                    oldQueueSize.set(messageCount);

                    if(delta > 0) {
                        int numOfInstancesToCreate = Math.min(delta,
                                webTierProperties.getMaxInstances() - runningInstanceCount);
                        log.info("Delta is Greater than 0, Creating " + numOfInstancesToCreate
                                + " instances to process images.");
                        ec2Helper.createInstance(numOfInstancesToCreate);
                    }
                    Try.run(() -> {
                        log.info("Sleeping for 2 Seconds");
                        Thread.sleep(2000);
                    });
                });
            }).onFailure(ex -> log.error(ex.getMessage()));
        }
    }

    @PostConstruct
    private void run() {
        log.info("Execute a thread to start Load Balancer");
        var executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Try.run(this::scale));
    }
}
