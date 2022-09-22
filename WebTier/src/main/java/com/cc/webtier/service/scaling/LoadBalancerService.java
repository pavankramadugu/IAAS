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
import java.util.Timer;
import java.util.TimerTask;

@Service
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadBalancerService {

    private final EC2Helper ec2Helper;

    private final SQSHelper sqsHelper;

    private final WebTierProperties webTierProperties;

    private void scaleInAndOut() {
        Try.run(() -> {
            var runningAppInstanceCount = ec2Helper.getInstancesCount() - 1;
            log.info("Number of Running App Instances: " + runningAppInstanceCount);

            sqsHelper.getSQSMessageCount().ifPresent(count -> {
                var messageCount = Integer.parseInt(count);
                log.info("Number of Messages in the Queue: " + messageCount);
                var maxPossibleInstances = webTierProperties.getMaxInstances() - runningAppInstanceCount;
                if (messageCount > 0 && messageCount > runningAppInstanceCount && maxPossibleInstances > 0) {
                    var numOfInstancesToCreate = Math.min(maxPossibleInstances,
                            messageCount - runningAppInstanceCount);
                    log.info("Delta is Greater than 0, Creating " + numOfInstancesToCreate
                            + " app instances to process images.");
                    ec2Helper.createInstance(numOfInstancesToCreate);
                }
            });
        }).onFailure(ex -> log.error(ex.getMessage()));
    }

    @PostConstruct
    private void loadBalancer() {
        log.info("Initializing a Timer Task to start Load Balancer");
        var task = new TimerTask() {

            @Override
            public void run() {
                log.info("Rerunning the Load Balancer");
                scaleInAndOut();
            }
        };

        var timer = new Timer();
        timer.schedule(task, 500, 2000);
    }
}
