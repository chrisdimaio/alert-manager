package io.chrisdima.http;

import io.chrisdima.http.eventpackage.EventPackage;
import io.chrisdima.http.eventpackage.EventPackageCodec;
import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.codecs.UppercaseRequestCodec;
import io.chrisdima.sdk.codecs.UppercaseResponseCodec;
import io.chrisdima.sdk.examples.UppercaseVerticle;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HTTPVerticle extends AbstractVerticle {
  private static final int DEFAULT_HTTP_PORT = 1234;
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );

  @Override
  public void start(Promise<Void> future) {
    vertx.eventBus().registerDefaultCodec(UppercaseRequest.class, new UppercaseRequestCodec());
    vertx.eventBus().registerDefaultCodec(UppercaseResponse.class, new UppercaseResponseCodec());

    // Need to figure out cluster factory before this works. -cluster
    vertx.deployVerticle(new UppercaseVerticle());

    logger.info(UppercaseRequest.class);

    Router router = Router.router(vertx);
    router.route().handler(this::handler);

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(
            config().getInteger("http.port", DEFAULT_HTTP_PORT),
            result -> {
              if (result.succeeded()) {
                future.complete();
              } else {
                future.fail(result.cause());
              }
            }
        );
  }

  public void handler(RoutingContext context) {
    String[] pathComponents = Helpers.getPathComponents(context.request().path(), NAMESPACE);
    String address = Helpers.createEventbusAddress(
        pathComponents[0], pathComponents[1], pathComponents[2]);

    vertx.eventBus().request(address, context.request().params().get("message"), reply -> {
      logger.info("Sending event to " + address);
      context
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end((String)reply.result().body());
    });


  }
}
