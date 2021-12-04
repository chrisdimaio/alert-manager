package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.pojos.LazyRequest;
import io.chrisdima.sdk.pojos.LazyResponse;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class LazyVerticle extends AbstractVerticle {
  private final static String EVENT = "lazy";
  private final static String VERSION = "v1";
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( LazyVerticle.class );

  @Override
  public void start(Promise<Void> future) {
    String address = Helpers.createEventbusAddress(EVENT, VERSION, NAMESPACE);
    logger.info("Listening to eventbus @ " + address);
    vertx.eventBus().consumer(address, message -> {

      LazyRequest request = (LazyRequest)message.body();
      logger.info("cookie crumb: " + request.getCookieCrumb());
      LazyResponse response = new LazyResponse();

      try {
        Thread.sleep(request.getDelay() * 1000);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }


      response.setCookieCrumb(request.getCookieCrumb());
      message.reply(response);
    });
  }
}
