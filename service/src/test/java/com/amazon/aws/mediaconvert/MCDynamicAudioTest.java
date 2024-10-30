package com.amazon.aws.mediaconvert;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MCDynamicAudioTest {

	private static MCDynamicAudio mcDynamicAudio;
	private static String region;
	private static String mcRoleArn;
	private static String mcQueueArn;
	private static String sourceBucket;
	private static String destinationBucket;


	@BeforeAll
	public static void setUp() throws IOException {

		try (InputStream input = MCDynamicAudioTest.class.getClassLoader()
										.getResourceAsStream("config.properties")) {
			Properties props = new Properties();
			if (input == null) {
				System.out.println("Sorry, unable to find config.properties");
				return;
			}

			props.load(input);
			mcRoleArn = props.getProperty("mcRoleArn");
			mcQueueArn = props.getProperty("mcQueueArn");
			region = props.getProperty("region");
			sourceBucket = props.getProperty("sourceBucket");
			destinationBucket = props.getProperty("destinationBucket");

			mcDynamicAudio = new MCDynamicAudio();
		} catch (IOException ex) {
			ex.printStackTrace();
		}				 
	}
	

	@Test
	@Tag("IntegrationTest")
	@Order(1)
	public void CreateJobWithZeroAudioTracks() {
		MediaConvertClient mc = mcDynamicAudio.getMediaConvertClientForEndpoint(region);
		String jobId = mcDynamicAudio.submitMediaConvertJob(mc, 
			sourceBucket + "SampleVideo-ZeroAudioTrack.mov", 
			destinationBucket, 0, mcRoleArn, mcQueueArn);
		assertNotNull(jobId);
		mc.close();
		System.out.println("Test CreateJob passed");
	}

	@Test
	@Tag("IntegrationTest")
	@Order(2)
	public void CreateJobWithSingleAudioTrack() {
		MediaConvertClient mc = mcDynamicAudio.getMediaConvertClientForEndpoint(region);
		String jobId = mcDynamicAudio.submitMediaConvertJob(mc, 
			sourceBucket + "SampleVideo-SingleAudioTrack.mp4", 
			destinationBucket, 1, mcRoleArn, mcQueueArn);
		assertNotNull(jobId);
		mc.close();
		System.out.println("Test CreateJob passed");
	}

	@Test
	@Tag("IntegrationTest")
	@Order(3)
	public void CreateJobWithMultipleAudioTrack() {
		MediaConvertClient mc = mcDynamicAudio.getMediaConvertClientForEndpoint(region);
		String jobId = mcDynamicAudio.submitMediaConvertJob(mc, 
			sourceBucket + "SampleVideo-MultiAudioTrack.mov", 
			destinationBucket, 4, mcRoleArn, mcQueueArn);
		assertNotNull(jobId);
		mc.close();
		System.out.println("Test CreateJob passed");
	}
}