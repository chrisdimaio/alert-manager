package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.examples.pojos.V1Filegetter;
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
/*
* Returns base 64 encoded version of file specified by filePath.
* */
public class FileGetterVerticle extends BaseVerticle {

  @Address("v1:filegetter")
  public void filegetter(Message<V1Filegetter> message) {
    Path filePath = Path.of(message.body().filePath);

    try {
      String data = new String(
          Base64.getEncoder().encode(Files.readAllBytes(filePath)), StandardCharsets.UTF_8);
      JsonObject response = new JsonObject().put("base64", data);
      String content_type = Files.probeContentType(filePath);
      message.reply(response, new DeliveryOptions()
          .addHeader("content_type",
              content_type != null ? content_type : "application/octet-stream" )
          .addHeader("is_base64", String.valueOf(true)));
    } catch (NoSuchFileException e) {
      logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
      message.fail(404, e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
