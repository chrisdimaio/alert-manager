package io.chrisdima.services;

import static io.chrisdima.sdk.Constants.CONTENT_TYPE_JPG;
import static io.chrisdima.sdk.Constants.DEFAULT_ID_COUNT;


import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.examples.pojos.V1Filegetter;
import io.chrisdima.utils.aws.AwsFactory;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/*
* Returns base 64 encoded version of file specified by filePath.
* */
public class FileGetterVerticle extends BaseVerticle {

  @Address("v1:get-from-s3")
  public void getFromS3(Message<JsonObject> message) {
    String key = message.body().getString("key");

    ResponseBytes<GetObjectResponse> objectBytes = null;
    try (S3Client s3Client = AwsFactory.s3Client()) {
      GetObjectRequest objectRequest = GetObjectRequest.builder().key(key).bucket("chrisdima.io")
          .build();
      objectBytes = s3Client.getObjectAsBytes(objectRequest);
    }

    String contentType = null;
    try {
      contentType = Files.probeContentType(Path.of(key));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String data = new String(
        Base64.getEncoder().encode(objectBytes.asByteArray()), StandardCharsets.UTF_8);
    JsonObject response = new JsonObject().put("base64", data);
    message.reply(response, new DeliveryOptions()
        .addHeader("content_type", contentType != null ? contentType : "application/octet-stream")
        .addHeader("is_base64", String.valueOf(true)));
  }

  @Address("v1:filegetter")
  public void filegetter(Message<V1Filegetter> message) {
    Path filePath = Path.of(message.body().filePath);

    try {
      String data = new String(
          Base64.getEncoder().encode(Files.readAllBytes(filePath)), StandardCharsets.UTF_8);
      JsonObject response = new JsonObject().put("base64", data);
      String contentType = Files.probeContentType(filePath);
      message.reply(response, new DeliveryOptions()
          .addHeader("content_type", contentType != null ? contentType : "application/octet-stream")
          .addHeader("is_base64", String.valueOf(true)));
    } catch (NoSuchFileException e) {
      logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
      message.fail(404, e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
