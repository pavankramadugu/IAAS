package com.cc.app;

import com.cc.app.classifier.ImageClassifier;
import com.cc.app.properties.AppTierProperties;
import com.cc.app.utils.AWSUtils;
import lombok.extern.java.Log;

@Log
public class AppTier {
    public static void main(String[] args) {
        log.info("Starting IAAS App Tier Application");
        new ImageClassifier(new AWSUtils(new AppTierProperties())).startClassifier();
        log.info("AppTier Instance Shutting Down");
    }
}
