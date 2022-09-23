package com.clocomp.apptier;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;


public class AppTierUtilities {

	/* 	 * Initialize credentials and details of the queues and S3 buckets 	* */
	private String Image_Bucket;
	private static final Logger System_Logger = LogManager.getLogger();
	private AWSCredentials UserCred;
	private String Result_Bucket;
	private AmazonS3 Bucket;
	private AmazonSQS Queue;
	private String Request_address;
	private String Response_address;



	public AppTierUtilities() {
		Response_address = GeneralUtil.SQS_RESPONSE_URL;
		Request_address = GeneralUtil.SQS_REQUEST_URL;
		UserCred = GeneralUtil.getAWSCREDENTIALS();
		Queue = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(UserCred))
				.withRegion(Regions.US_EAST_1).build();

		Result_Bucket =GeneralUtil.S3_ResultsBucket;
		Image_Bucket =GeneralUtil.S3_ImagesBucket;
		Bucket = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(UserCred))
				.withRegion(Regions.US_EAST_1).build();
	}

	public Path S3Image(String ImgN) throws IOException {
		S3Object Obj_S3 = Bucket.getObject(Image_Bucket, ImgN);
		S3ObjectInputStream Stream_s3 = Obj_S3.getObjectContent();
		Path OpPath = Paths.get(ImgN);
		long ImgS = Files.copy(Stream_s3, OpPath, StandardCopyOption.REPLACE_EXISTING);
		return OpPath;
	}

	public void MessageQueueS(String str) {
		Queue.MessageQueue(Response_address, str);
	}

	/* Reads message from poll and terminates instance when queue is empty to scale down system */

	public Message MessageQueueR() {
		ReceiveMessageRequest RMessage = new ReceiveMessageRequest()
				.withQueueUrl(Request_address)
				.withMaxNumberOfMessages(1)
				.withWaitTimeSeconds(15);
		ReceiveMessageResult result = Queue.receiveMessage(RMessage);
		List<Message> MessageRow=result.getMessages();
		if(MessageRow == null || MessageRow.isEmpty()) {
			System_Logger.info("SQS Empty, thus killing app-tier instance");
			Queue.shutdown();
			Bucket.shutdown();
			terminateThisEC2instance();
			return null;
		}

		System_Logger.info("Queue messages {} ",msgList.size());
		Message message = msgList.get(0);
		System_Logger.info("Messages received {} ",message.getBody());
		return message;

	}

	public void MessageRemoval(Message messag) {
		String MessageContent = messag.getBody();
		String RecipientHandle = messag.getReceiptHandle();
		DeleteMessageRequest DelMessage = new DeleteMessageRequest().withQueueUrl(Request_address)
				.withReceiptHandle(MessageContent);
		DeleteMessageResult res = Queue.deleteMessage(DelMessage);
		System_Logger.info("Messages Removed: {}",MessageContent);
	}
	public void saveMessageResultinS3(String Object, String Res) {
		Bucket.putObject(Result_Bucket, Object, Res);
		System_Logger.info("Objects stored in s3 images: {} result: {}",imageName,result);
	}
	
	public void KillInstance() {
		Runtime command = Runtime.getRuntime();
		String str="sudo shutdown -h now";
		try {
			Process pr = run.exec(str);
		} catch (IOException obj) {
			obj.printStackTrace();
		}
		System.exit(0);
	}
}
