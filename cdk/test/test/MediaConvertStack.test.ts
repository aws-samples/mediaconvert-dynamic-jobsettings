import { App } from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { MediaConvertStack } from '../lib/MediaConvertStack';

test('MediaConvert Stack', () => {
    const app = new App();
    const stack = new MediaConvertStack(app, 'TestStack');

    const template = Template.fromStack(stack);

    // Add assertions to test the stack resources
    template.resourceCountIs('AWS::S3::Bucket', 2);
    template.resourceCountIs('AWS::IAM::Role', 1);
    template.resourceCountIs('AWS::MediaConvert::Queue', 1);
});
