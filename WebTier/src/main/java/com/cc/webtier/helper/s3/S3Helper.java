package com.cc.webtier.helper.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cc.webtier.properties.WebTierProperties;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class S3Helper {

    private final WebTierProperties webTierProperties;

    public S3Helper(WebTierProperties webTierProperties) {
        this.webTierProperties = webTierProperties;
    }

    public List<String> uploadImagesToS3(MultipartFile[] images) {
        return Try.of(() -> {
            var amazonS3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            log.info("Uploading Images to S3");
                    var fileList = new ArrayList<String>();

                    Arrays.stream(images).forEach(image -> {
                        var fileName = image.getOriginalFilename();
                        Try.withResources(image::getInputStream)
                                .of(inputStream -> amazonS3.putObject(webTierProperties.getRequestS3Bucket(), fileName,
                                inputStream, null))
                                .onFailure(ex -> log.error(ex.getMessage()));
                        fileList.add(fileName);
                    });
                    log.info("Successfully Uploaded Images to S3");
                    amazonS3.shutdown();
                    return fileList;
                })
                .onFailure(ex -> log.error(ex.getMessage()))
                .getOrElse(new ArrayList<>());
    }
}
