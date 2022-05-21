package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
/*
* Returns base 64 encoded version of file specified by filePath.
* */
public class FileGetterVerticle extends BaseVerticle {
  private final static String EVENT = "filegetter";
  private final static String VERSION = "v1";
  private final static String NAMESPACE = "internal";

  @Override
  public void start(Promise<Void> future) {
    logger = LoggerFactory.getLogger( FileGetterVerticle.class );

    String address = Helpers.createEventbusAddress(EVENT, VERSION, NAMESPACE);
    publishService(this.getClass().getName(), address);

    logger.info("Listening to eventbus @ " + address);
    vertx.eventBus().consumer(address, message -> {
      JsonObject request = (JsonObject)message.body();
      Path filePath = Path.of(request.getString("filePath"));

      try {
        String data = new String(
            Base64.getEncoder()
                .encode(Files.readAllBytes(filePath)),
            StandardCharsets.UTF_8);
        JsonObject response = new JsonObject().put("base64", data);
        String content_type = Files.probeContentType(filePath);
        message.reply(response, new DeliveryOptions()
            .addHeader("content_type",
                content_type != null ? content_type : "application/octet-stream" )
            .addHeader("is_base64", String.valueOf(true)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
