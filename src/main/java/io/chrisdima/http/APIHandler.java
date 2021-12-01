package io.chrisdima.http;

import io.chrisdima.sdk.Helpers;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.Arrays;

public class APIHandler implements Handler<RoutingContext> {
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );

  @Override
  public void handle(RoutingContext context) {
    String[] pathComponents = Helpers.getPathComponents(context.request().path(), NAMESPACE);
    String eventbusAddress = Helpers.createEventbusAddress(
        pathComponents[0], pathComponents[1], pathComponents[2]);

//    Vertx.vertx().eventBus().send("hello", "hello");

    logger.info("eventbus address: " + eventbusAddress);

    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily("success"));
  }
}
