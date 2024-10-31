#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { MediaConvertStack } from '../lib/MediaConvertStack';
import { AWS_ACCOUNT, AWS_REGION } from '../config';

const app = new cdk.App();

if (!AWS_ACCOUNT || !AWS_REGION) {
    throw new Error('AWS account and region must be set in the environment variables AWS_ACCOUNT and AWS_REGION');
}

new MediaConvertStack(app, 'MediaConvertStack', {
    env: {
        account: AWS_ACCOUNT,
        region: AWS_REGION,
    },
});
