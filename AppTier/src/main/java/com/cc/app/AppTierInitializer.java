package com.clocomp.apptier;

import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

import java.io.IOException;

import com.amazonaws.services.sqs.model.Message;

public class AppTierInitializer {
	private boolean Flagthread = false;
	private AppTierUtilities AWSUtilities;
	private static final Logger SystemLogger = LogManager.getLogger();

	public AppTierInitializer(){
		this.AWSUtilities = new AppTierUtilities();
	}

	public void run() {
		while (Flagthread != true) {
			try {
				Message text = AWSUtilities.readMessage();
				Path ImagLocat = AWSUtilities.downloadImageFromS3(text.getBody());
				String ImageRes=PhotoRecognition(ImagLocat.toAbsolutePath().toString());
				String dispName = text.getBody();
				AWSUtilities.saveMessageResultinS3(dispName,ImageRes);
				AWSUtilities.sendMessage(dispName + "," + ImageRes);
				AWSUtilities.deleteMessage(text);
			} catch (Exception e) {
				//all exception caught here, if any exception terminate this EC2 instance
				SystemLogger.error("Occuring Exception: ", e);
				Flagthread();
				AWSUtilities.terminateThisEC2instance();
			}
		}
	}

	public void Flagthread() {
		this.Flagthread = true;
	}

	/*
	 * Runs the Py script for image recognition and gets the result.
	 * */
	private String PhotoRecognition(String ImagePath) {
		String Image_Res = "";
		//python3 /home/ubuntu/classifier/image_classification.py path_to_the_image
		String line="python3 image_classification.py "+ ImagePath;
		SystemLogger.info("command is : {}",line);
		try {
			Runtime run = Runtime.getRuntime();
			Process process = run.exec(line);
			boolean Boolstatus = process.waitFor(90, TimeUnit.SECONDS);
			if (Boolstatus == true) {
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				Image_Res = buffReader.readLine();
				buffReader.close();
			} else {
				Image_Res = "Image Recognition Timeout: No Result";
			}
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
		SystemLogger.info("Result of Recognition of image: {} is: {}",ImagePath,Image_Res);
		return Image_Res;
	}
	
}
