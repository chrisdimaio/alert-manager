package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class UppercaseVerticle extends AbstractVerticle {
  private final static String EVENT = "uppercase";
  private final static String VERSION = "v1";
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( UppercaseVerticle.class );

  @Override
  public void start(Promise<Void> future) {
    String address = Helpers.createEventbusAddress(EVENT, VERSION, NAMESPACE);
    logger.info("Listening to eventbus @ " + address);
    vertx.eventBus().consumer(address, message -> {

      UppercaseRequest word = (UppercaseRequest)message.body();
      logger.info("cookie crumb: " + word.getCookieCrumb());
      UppercaseResponse uppercaseResponse = new UppercaseResponse();
      uppercaseResponse.setMessage(word.getMessage().toUpperCase());
      uppercaseResponse.setCookieCrumb(word.getCookieCrumb());
      message.reply(uppercaseResponse);
    });
  }
}
