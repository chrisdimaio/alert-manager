package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UppercaseVerticle extends BaseVerticle {
  private final static String EVENT = "uppercase";
  private final static String VERSION = "v1";
  private final static String NAMESPACE = "internal";

  @Override
  public void start(Promise<Void> future) {
    logger = LoggerFactory.getLogger( UppercaseVerticle.class );

    String address = Helpers.createEventbusAddress(EVENT, VERSION, NAMESPACE);
    publishService(this.getClass().getName(), address);

    logger.info("Listening to eventbus @ " + address);
    vertx.eventBus().consumer(address, message -> {

      UppercaseRequest word = (UppercaseRequest)message.body();
      logger.info("cookie crumb: " + word.getCookieCrumb());
      UppercaseResponse uppercaseResponse = new UppercaseResponse();
      uppercaseResponse.setMessage(word.getMessage().toUpperCase());
      uppercaseResponse.setCookieCrumb(word.getCookieCrumb());
      message.reply(uppercaseResponse);
    });

    vertx.eventBus().consumer("internal:v1:image", message -> {
      UppercaseResponse response = new UppercaseResponse();
      response.setIsBinary(true);
      try {
        UppercaseRequest request = (UppercaseRequest)message.body();
        response.setBuffer(Buffer.buffer(Files.readAllBytes(
            new File(request.getMessage()).toPath())));
      } catch (IOException e) {
        e.printStackTrace();
      }
      message.reply(response);
    });
  }
}
