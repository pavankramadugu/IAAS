package com.cc.app.classifier;

import com.cc.app.utils.AWSUtils;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
@RequiredArgsConstructor
public class ImageClassifier {

    private final AWSUtils awsUtils;

    public void startClassifier() {
        var loop = new AtomicBoolean(true);
        while (loop.get()) {
            Try.run(() -> {
                var message = awsUtils.readMessageFromSQS();
                String imageName = message.getBody();

                awsUtils.downloadFromS3(message.getBody());

                String recognitionResult = getResult(imageName);

                awsUtils.saveInS3(imageName, recognitionResult);

                awsUtils.sendMessageToSQS(recognitionResult);

                awsUtils.deleteMessageFromSQS(message);
            }).onFailure(ex -> {
                log.severe(ex.getMessage());
                awsUtils.terminateEC2();
                loop.set(false);
            });
        }
    }

    private String getResult(String imagePath) {
        return Try.of(() -> {
                    String command = "python3 image_classification.py " + imagePath;
                    log.info("Command being executed on AppTier: " + command);

                    Runtime runtime = Runtime.getRuntime();
                    Process process = runtime.exec(command);
                    boolean processStatus = process.waitFor(100, TimeUnit.SECONDS);
                    if (processStatus) {
                        return Try.withResources(() ->
                                        new BufferedReader(new InputStreamReader(process.getInputStream())))
                                .of(BufferedReader::readLine).get();
                    }
                    return "Timeout for image recognition passed no result";
                })
                .onFailure(ex -> log.severe(ex.getMessage()))
                .get();
    }
}
