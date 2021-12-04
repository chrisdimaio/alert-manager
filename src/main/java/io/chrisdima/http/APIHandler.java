package io.chrisdima.http;

import io.chrisdima.sdk.Helpers;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class APIHandler implements Handler<RoutingContext> {
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );

  @Override
  public void handle(RoutingContext context) {
    String eventbusAddress = Helpers.createEventbusAddress(context, NAMESPACE);

    logger.info("eventbus address: " + eventbusAddress);

    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily("success"));
  }
}
