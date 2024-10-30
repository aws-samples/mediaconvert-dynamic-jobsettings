package com.amazon.aws.mediaconvert;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.AacAudioDescriptionBroadcasterMix;
import software.amazon.awssdk.services.mediaconvert.model.AacCodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.AacCodingMode;
import software.amazon.awssdk.services.mediaconvert.model.AacRateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.AacRawFormat;
import software.amazon.awssdk.services.mediaconvert.model.AacSettings;
import software.amazon.awssdk.services.mediaconvert.model.AacSpecification;
import software.amazon.awssdk.services.mediaconvert.model.AfdSignaling;
import software.amazon.awssdk.services.mediaconvert.model.AntiAlias;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodec;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioDefaultSelection;
import software.amazon.awssdk.services.mediaconvert.model.AudioDescription;
import software.amazon.awssdk.services.mediaconvert.model.AudioLanguageCodeControl;
import software.amazon.awssdk.services.mediaconvert.model.AudioSelector;
import software.amazon.awssdk.services.mediaconvert.model.AudioSelectorType;
import software.amazon.awssdk.services.mediaconvert.model.AudioTypeControl;
import software.amazon.awssdk.services.mediaconvert.model.ColorMetadata;
import software.amazon.awssdk.services.mediaconvert.model.ContainerSettings;
import software.amazon.awssdk.services.mediaconvert.model.ContainerType;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.DeinterlaceAlgorithm;
import software.amazon.awssdk.services.mediaconvert.model.Deinterlacer;
import software.amazon.awssdk.services.mediaconvert.model.DeinterlacerControl;
import software.amazon.awssdk.services.mediaconvert.model.DeinterlacerMode;
import software.amazon.awssdk.services.mediaconvert.model.DestinationSettings;
import software.amazon.awssdk.services.mediaconvert.model.DropFrameTimecode;
import software.amazon.awssdk.services.mediaconvert.model.FileGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264AdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.H264EntropyEncoding;
import software.amazon.awssdk.services.mediaconvert.model.H264FieldEncoding;
import software.amazon.awssdk.services.mediaconvert.model.H264GopBReference;
import software.amazon.awssdk.services.mediaconvert.model.H264GopSizeUnits;
import software.amazon.awssdk.services.mediaconvert.model.H264InterlaceMode;
import software.amazon.awssdk.services.mediaconvert.model.H264ParControl;
import software.amazon.awssdk.services.mediaconvert.model.H264RateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.H264RepeatPps;
import software.amazon.awssdk.services.mediaconvert.model.H264SceneChangeDetect;
import software.amazon.awssdk.services.mediaconvert.model.H264Settings;
import software.amazon.awssdk.services.mediaconvert.model.H264Syntax;
import software.amazon.awssdk.services.mediaconvert.model.H264UnregisteredSeiTimecode;
import software.amazon.awssdk.services.mediaconvert.model.Input;
import software.amazon.awssdk.services.mediaconvert.model.InputRotate;
import software.amazon.awssdk.services.mediaconvert.model.InputTimecodeSource;
import software.amazon.awssdk.services.mediaconvert.model.JobSettings;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.MediaConvertException;

import software.amazon.awssdk.services.mediaconvert.model.Mp4CslgAtom;
import software.amazon.awssdk.services.mediaconvert.model.Mp4FreeSpaceBox;
import software.amazon.awssdk.services.mediaconvert.model.Mp4MoovPlacement;
import software.amazon.awssdk.services.mediaconvert.model.Mp4Settings;
import software.amazon.awssdk.services.mediaconvert.model.Output;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroup;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupType;
import software.amazon.awssdk.services.mediaconvert.model.RespondToAfd;
import software.amazon.awssdk.services.mediaconvert.model.S3DestinationAccessControl;
import software.amazon.awssdk.services.mediaconvert.model.S3DestinationSettings;
import software.amazon.awssdk.services.mediaconvert.model.S3ObjectCannedAcl;
import software.amazon.awssdk.services.mediaconvert.model.ScalingBehavior;
import software.amazon.awssdk.services.mediaconvert.model.TimecodeConfig;
import software.amazon.awssdk.services.mediaconvert.model.TimecodeSource;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodec;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.VideoDescription;
import software.amazon.awssdk.services.mediaconvert.model.VideoPreprocessor;
import software.amazon.awssdk.services.mediaconvert.model.VideoSelector;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


public class MCDynamicAudio {
    
		private static final String AUDIO_SELECTOR = "Audio Selector ";
		private static final String MP4_EXTENSTION = ".mp4";
		private static final String EMPTY_STRING = "";

    public static void main(String[] args) {

				if (args.length != 6) {
						System.out.println("Usage: MCDynamicAudio <input file> <output file> <number of audio tracks>" +
						 " <mediaConvertRoleArn> <mediaConvertQueueArn> <region>");
						System.exit(1);
				}
				MCDynamicAudio mcDynamicAudio = new MCDynamicAudio();
        MediaConvertClient emc = mcDynamicAudio.getMediaConvertClientForEndpoint(args[5]);

        mcDynamicAudio.submitMediaConvertJob(emc, args[0], args[1], Integer.parseInt(args[2]),
												args[3], args[4]);
        emc.close();
    }


    public String submitMediaConvertJob(MediaConvertClient emc,
                                       String sourceFilePath, 
																			 String destinationPath, 
																			 int noOfAudioTracks,
																			 String mediaConvertRoleArn,
																			 String mediaConvertQueueArn) {

        final CreateJobRequest jobRequest = CreateJobRequest.builder()
                .settings(getJobSettings(sourceFilePath, destinationPath, noOfAudioTracks))
                .role(mediaConvertRoleArn)
                .queue(mediaConvertQueueArn)
                .build();
        CreateJobResponse createJobResponse = emc.createJob(jobRequest);
				String jobId = createJobResponse.job().id();
        System.out.println("Media Convert Job Id" + jobId);
				return jobId;
    }

    public MediaConvertClient getMediaConvertClientForEndpoint(String region) {
			MediaConvertClient mc = MediaConvertClient.builder()
			.region(Region.of(region))
			.build();

			DescribeEndpointsResponse res = mc.describeEndpoints(DescribeEndpointsRequest.builder()
							.maxResults(20)
							.build());

			if (res.endpoints().size() <= 0) {
					System.out.println("Cannot find MediaConvert service endpoint URL!");
					System.exit(1);
			}

			String endpointURL = res.endpoints().get(0).url();
			System.out.println("Endpoint URL->" + endpointURL);

			
			return MediaConvertClient.builder()
							.region(Region.of(region))
							.endpointOverride(URI.create(endpointURL))
							.build(); 
    }

    private AudioDescription getAudioDescription(final String audioSourceName) {
        return AudioDescription.builder().audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                .audioSourceName(audioSourceName)
                .codecSettings(AudioCodecSettings.builder()
                    .codec(AudioCodec.AAC)
                    .aacSettings(AacSettings.builder()
                        .audioDescriptionBroadcasterMix(AacAudioDescriptionBroadcasterMix.NORMAL)
                        .bitrate(128000)
                        .rateControlMode(AacRateControlMode.CBR)
                        .codecProfile(AacCodecProfile.LC)
                        .codingMode(AacCodingMode.CODING_MODE_2_0)
                        .rawFormat(AacRawFormat.NONE)
                        .sampleRate(48000)
                        .specification(AacSpecification.MPEG4)
                        .build())
                    .build())
                .languageCodeControl(AudioLanguageCodeControl.FOLLOW_INPUT)
                .audioType(0)
                .build();
    }

    private List<AudioDescription> getAudioDescriptions(int noOfAudioTracks) {
        if (noOfAudioTracks == 0) return null;

        List<AudioDescription> audioDescriptions = new ArrayList<>();
        if (noOfAudioTracks == 1)
            audioDescriptions.add(getAudioDescription(AUDIO_SELECTOR + 1));
        else {
            IntStream.rangeClosed(1, noOfAudioTracks + 1).forEach(i -> {
                audioDescriptions.add(getAudioDescription(AUDIO_SELECTOR + i));
            });
        }
        return audioDescriptions;
    }

    private Input getInputs(String sourceFilePath, int noOfAudioTracks) {

        Map<String, AudioSelector> audioSelectorMap = null;

        if (noOfAudioTracks > 0) {
            audioSelectorMap = new HashMap<>();
            //first audio selector will include all tracks
            final List<Integer> allTracks = new ArrayList<>();
            IntStream.rangeClosed(1, noOfAudioTracks).forEach(allTracks::add);
            AudioSelector audioSelector = AudioSelector.builder()
                    .defaultSelection(AudioDefaultSelection.DEFAULT)
                    .selectorType(AudioSelectorType.TRACK)
                    .tracks(allTracks)
                    .build();
            audioSelectorMap.put(AUDIO_SELECTOR + "1", audioSelector);

            // second selector and onwards will have tracks from 1..noOfAudioTracks
            // only if it is multi-track
            if (noOfAudioTracks > 1) {
                for (int i = 1; i <= noOfAudioTracks; i++) {
                    AudioSelector aSelector = AudioSelector.builder()
                            .defaultSelection(AudioDefaultSelection.DEFAULT)
                            .selectorType(AudioSelectorType.TRACK)
                            .tracks(i)
                            .build();
                    audioSelectorMap.put(AUDIO_SELECTOR + (i +1), aSelector);
                }
            }
        }

        return Input.builder()
                .fileInput(sourceFilePath)
                .audioSelectors(audioSelectorMap)
                .videoSelector(VideoSelector.builder()
                    .rotate(InputRotate.AUTO).build())
                .timecodeSource(InputTimecodeSource.ZEROBASED)
                .build();
    }

    private JobSettings getJobSettings(String sourceFilePath, String s3DestinationPath, int noOfAudioTracks) {

        JobSettings jobSettings = null;
        try {
            VideoCodecSettings codecSettings = VideoCodecSettings.builder()
                .codec(VideoCodec.H_264)
                .h264Settings(H264Settings.builder()
                    .interlaceMode(H264InterlaceMode.PROGRESSIVE)
                    .parNumerator(1)
                    .numberReferenceFrames(3)
                    .syntax(H264Syntax.DEFAULT)
                    .gopClosedCadence(1)
                    .hrdBufferInitialFillPercentage(90)
                    .gopSize(30.0)
                    .slices(1)
                    .gopBReference(H264GopBReference.DISABLED)
                    .hrdBufferSize(12750000)
                    .parDenominator(1)
                    .entropyEncoding(H264EntropyEncoding.CABAC)
                    .bitrate(2000000)
                    .rateControlMode(H264RateControlMode.CBR)
                    .codecProfile(H264CodecProfile.HIGH)
                    .minIInterval(0)
                    .adaptiveQuantization(H264AdaptiveQuantization.AUTO)
                    .codecLevel(H264CodecLevel.AUTO)
                    .fieldEncoding(H264FieldEncoding.PAFF)
                    .sceneChangeDetect(H264SceneChangeDetect.ENABLED)
                    .unregisteredSeiTimecode(H264UnregisteredSeiTimecode.DISABLED)
                    .gopSizeUnits(H264GopSizeUnits.FRAMES)
                    .parControl(H264ParControl.SPECIFIED)
                    .numberBFramesBetweenReferenceFrames(1)
                    .repeatPps(H264RepeatPps.DISABLED)
                    .build())
                .build();



            VideoDescription videoDescription = VideoDescription.builder()
                .scalingBehavior(ScalingBehavior.DEFAULT)
                .videoPreprocessors(VideoPreprocessor.builder()
                    .deinterlacer(Deinterlacer.builder()
                            .algorithm(DeinterlaceAlgorithm.INTERPOLATE)
                            .mode(DeinterlacerMode.DEINTERLACE)
                            .control(DeinterlacerControl.NORMAL)
                            .build())
                    .build())
                .antiAlias(AntiAlias.ENABLED)
                .sharpness(50)
                .codecSettings(codecSettings)
                .afdSignaling(AfdSignaling.NONE)
                .dropFrameTimecode(DropFrameTimecode.ENABLED)
                .respondToAfd(RespondToAfd.NONE)
                .colorMetadata(ColorMetadata.INSERT)
                .build();
            ContainerSettings containerSettings = ContainerSettings.builder()
                .container(ContainerType.MP4)
                .mp4Settings(Mp4Settings.builder()
                    .cslgAtom(Mp4CslgAtom.INCLUDE)
                    .freeSpaceBox(Mp4FreeSpaceBox.EXCLUDE)
                    .moovPlacement(Mp4MoovPlacement.PROGRESSIVE_DOWNLOAD)
                    .build())
                .build();

            Output output = Output.builder()
                .nameModifier("-Transcoded")
                .videoDescription(videoDescription)
                .audioDescriptions(getAudioDescriptions(noOfAudioTracks))
                .containerSettings(containerSettings)
                .build();

            OutputGroup outputGroup = OutputGroup.builder()
                    .customName("Video Proxy")
                    .name("File Group")
                    .outputs(output)
                    .outputGroupSettings(OutputGroupSettings.builder()
                        .type(OutputGroupType.FILE_GROUP_SETTINGS)
                        .fileGroupSettings(FileGroupSettings.builder()
                            .destination(s3DestinationPath)
                            .destinationSettings(DestinationSettings.builder()
                                    .s3Settings(S3DestinationSettings.builder()
                                        .accessControl(S3DestinationAccessControl.builder()
                                            .cannedAcl(S3ObjectCannedAcl.BUCKET_OWNER_FULL_CONTROL)
                                            .build())
                                        .build())
                                .build())
                            .build())
                        .build())
                    .build();
            jobSettings = JobSettings.builder()
                    .timecodeConfig(TimecodeConfig.builder()
                        .source(TimecodeSource.ZEROBASED)
                        .build())
                    .outputGroups(outputGroup)
                    .inputs(getInputs(sourceFilePath, noOfAudioTracks))
                    .build();
        } catch (MediaConvertException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return jobSettings;
    }
}
