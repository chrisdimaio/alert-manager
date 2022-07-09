package io.chrisdima.utils.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

//    https://aws.amazon.com/blogs/developer/tuning-the-aws-java-sdk-2-x-to-reduce-startup-time/
public class AwsFactory {
  private static final Region REGION = Region.US_EAST_1;

  public static DynamoDBClient dynamoClient() {
    return new DynamoDBClient(REGION);
  }

  public static S3Client s3Client() {
    return S3Client.builder()
        .region(REGION)
        .build();
  }
//  Add this later
//  public static SNSClient snsClient()

}
