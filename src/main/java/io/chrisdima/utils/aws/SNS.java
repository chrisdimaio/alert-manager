package io.chrisdima.utils.aws;

import java.util.Map;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javav2/example_code/sns/src/main/java/com/example/sns/PublishTextSMS.java
public class SNS {
  public static void main(String[] args) {

    sendSMS("some ting wong", "+19784761823");
  }
  public static void sendSMS(String message, String phoneNumber) {
    try (SnsClient snsClient = SnsClient.builder()
        .region(Region.US_EAST_1)
        .build()) {
      PublishRequest request = PublishRequest.builder()
          .message(message)
          .phoneNumber(phoneNumber)
          .messageAttributes(
              Map.of(
                  "AWS.MM.SMS.OriginationNumber",
                  MessageAttributeValue.builder()
                      .dataType("String")
                      .stringValue(System.getenv("AWS.MM.SMS.OriginationNumber"))
                      .build()))
          .build();

      PublishResponse result = snsClient.publish(request);
      System.out.println(result.messageId() + " Message sent. Status was " +
          result.sdkHttpResponse().statusCode());

    } catch (SnsException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
    }
  }


}
