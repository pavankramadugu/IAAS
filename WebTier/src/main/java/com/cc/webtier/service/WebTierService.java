package com.cc.webtier.service;

import com.amazonaws.services.sqs.model.Message;
import com.cc.webtier.helper.s3.S3Helper;
import com.cc.webtier.helper.sqs.SQSHelper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebTierService {

    private final Map<String, String> imageModelResultMap = new HashMap<>();

    private final SQSHelper sqsHelper;

    private final S3Helper s3Helper;

    public Map<String, String> processImages(MultipartFile[] images) {
        return Try.of(() -> {
                    var imageNames = s3Helper.uploadImagesToS3(images);

                    imageNames.forEach(sqsHelper::publishMessage);

                    return getResultsFromResponseQueue(imageNames);
                }).onFailure(ex -> log.error(ex.getMessage()))
                .getOrElse(new HashMap<>());
    }

    public Map<String, String> getResults() {
        return imageModelResultMap;
    }

    private Map<String, String> getResultsFromResponseQueue(List<String> imageNames) {
        while (!imageModelResultMap.keySet().containsAll(imageNames)) {
            log.info("Waiting to read classification response from Response Queue");
            List<Message> resultMessages = sqsHelper.readMessagesFromSQS();

            resultMessages.forEach(message -> {
                var values = message.getBody().split(",");
                log.info("Setting Response in Map");
                imageModelResultMap.put(values[1], values[2]);
                sqsHelper.deleteMessage(message);
            });
        }

        return imageNames.stream()
                .filter(imageModelResultMap::containsKey)
                .collect(Collectors.toMap(Function.identity(), imageModelResultMap::get));
    }
}
