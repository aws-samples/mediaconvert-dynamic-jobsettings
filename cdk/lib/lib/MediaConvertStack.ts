import {CfnOutput, RemovalPolicy, Stack, StackProps} from 'aws-cdk-lib';
import {Construct} from 'constructs';
import {Bucket, BucketEncryption} from 'aws-cdk-lib/aws-s3';
import {Effect, PolicyDocument, PolicyStatement, Role, ServicePrincipal} from 'aws-cdk-lib/aws-iam';
import {CfnQueue} from 'aws-cdk-lib/aws-mediaconvert';

export class MediaConvertStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        //Create S3 Bucket
        const sourceBucket = new Bucket(this, 'video-transcoding-source', {
            removalPolicy: RemovalPolicy.DESTROY, //Change to RETAIN for production
            autoDeleteObjects: true, //Change to false for production
            enforceSSL: true,
            encryption: BucketEncryption.S3_MANAGED,
        });
        const destinationBucket = new Bucket(this, 'video-transcoding-destination', {
            removalPolicy: RemovalPolicy.DESTROY, //Change to RETAIN for production
            autoDeleteObjects: true, //Change to false for production
            enforceSSL: true,
            encryption: BucketEncryption.S3_MANAGED,
        });

        //MediaConvert Policy Document
        const mediaConvertPolicyDocument = new PolicyDocument({
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['cloudwatch:PutMetricData', 'cloudwatch:PutMetricAlarm', 'logs:CreateLogGroup', 'logs:CreateLogStream', 'logs:PutLogEvents'],
                    resources: [
                        `arn:aws:logs:${this.region}:${this.account}:log-group:/aws/mediaconvert/*`,
                        `arn:aws:cloudwatch:${this.region}:${this.account}:*`,
                    ],
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['s3:GetObject', 's3:GetBucketLocation', 's3:ListBucket'],
                    resources: [sourceBucket.bucketArn, `${sourceBucket.bucketArn}/*`],
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['s3:PutObject', 's3:PutObjectAcl'],
                    resources: [destinationBucket.bucketArn, `${destinationBucket.bucketArn}/*`],
                }),
            ],
        });

        //IAM Role for MediaConvert
        const videoTranscodeRole = new Role(this, 'VideoTranscodeRole', {
            assumedBy: new ServicePrincipal('mediaconvert.amazonaws.com'),
            inlinePolicies: {mediaConvertPolicy: mediaConvertPolicyDocument}
        });

        videoTranscodeRole.addToPolicy(new PolicyStatement({
            resources: ['*'],
            actions: ['sts:AssumeRole'],
        }));

        // Add the iam:PassRole permission to the MediaConvert role
        videoTranscodeRole.addToPolicy(
            new PolicyStatement({
                effect: Effect.ALLOW,
                actions: ['iam:PassRole'],
                resources: [`arn:aws:iam::${this.account}:role/${videoTranscodeRole.roleName}`],
            })
        );

        //MediaConvert Queue
        const VideoTranscodeQueue = new CfnQueue(this, 'VideoTranscode', {
            description: 'Video Transcode job queue',
            pricingPlan: 'ON_DEMAND',
            status: 'ACTIVE'
        });

        // Print bucket names
        new CfnOutput(this, 'SourceBucketName', {value: sourceBucket.bucketName});
        new CfnOutput(this, 'DestinationBucketName', {value: destinationBucket.bucketName});

        // Print MediaConvert job queue ARN
        new CfnOutput(this, 'MediaConvertJobQueueArn', {value: VideoTranscodeQueue.attrArn});

        // Print MediaConvert role ARN
        new CfnOutput(this, 'MediaConvertRoleArn', {value: videoTranscodeRole.roleArn});
    }
}
