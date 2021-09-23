package io.chrisdima.utils.aws;

import software.amazon.awssdk.regions.Region;

//    https://aws.amazon.com/blogs/developer/tuning-the-aws-java-sdk-2-x-to-reduce-startup-time/
public class AwsFactory {
  private static final Region REGION = Region.US_EAST_1;

  public static DynamoDBClient dynamoClient() {
    return new DynamoDBClient(REGION);
  }
//  Add this later
//  public static SNSClient snsClient()

}
