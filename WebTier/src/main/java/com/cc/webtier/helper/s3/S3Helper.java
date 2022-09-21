package com.cc.webtier.helper.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
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

    private final AmazonS3 amazonS3;

    public S3Helper(WebTierProperties webTierProperties) {
        this.webTierProperties = webTierProperties;
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();
        ;
    }

    public List<String> uploadImagesToS3(MultipartFile[] images) {
        return Try.of(() -> {
            log.info("Uploading Images to S3");
                    var fileList = new ArrayList<String>();

                    Arrays.stream(images).forEach(image -> {
                        var fileName = image.getOriginalFilename();
                        Try.run(() -> amazonS3.putObject(webTierProperties.getRequestS3Bucket(), fileName,
                                        image.getInputStream(), null))
                                .onFailure(ex -> log.error(ex.getMessage()));
                        fileList.add(fileName);
                    });
                    log.info("Successfully Uploaded Images to S3");
                    return fileList;
                })
                .onFailure(ex -> log.error(ex.getMessage()))
                .getOrElse(new ArrayList<>());
    }
}
