package io.chrisdima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.CookieCrumbGenerator;
import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.codecs.LazyRequestCodec;
import io.chrisdima.sdk.codecs.LazyResponseCodec;
import io.chrisdima.sdk.codecs.UppercaseRequestCodec;
import io.chrisdima.sdk.codecs.UppercaseResponseCodec;
import io.chrisdima.sdk.examples.LazyVerticle;
import io.chrisdima.sdk.examples.UppercaseVerticle;
import io.chrisdima.sdk.pojos.BasePojo;
import io.chrisdima.sdk.pojos.LazyRequest;
import io.chrisdima.sdk.pojos.LazyResponse;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HTTPVerticle extends AbstractVerticle {
  private static final int DEFAULT_HTTP_PORT = 1234;
  private final static String NAMESPACE = "internal";

  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );
  private final CookieCrumbGenerator cookieCrumbGenerator = new CookieCrumbGenerator();

  @Override
  public void start(Promise<Void> future) {
    vertx.eventBus().registerDefaultCodec(UppercaseRequest.class, new UppercaseRequestCodec());
    vertx.eventBus().registerDefaultCodec(UppercaseResponse.class, new UppercaseResponseCodec());
    vertx.eventBus().registerDefaultCodec(LazyRequest.class, new LazyRequestCodec());
    vertx.eventBus().registerDefaultCodec(LazyResponse.class, new LazyResponseCodec());

    // Need to figure out cluster factory before this works. -cluster
    vertx.deployVerticle(new UppercaseVerticle());
    vertx.deployVerticle(new LazyVerticle());

    logger.info(UppercaseRequest.class);

    Router router = Router.router(vertx);
    router.route("/*").handler(BodyHandler.create());
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

    Object request = null;
    try {
      request = createRequest(context);
    } catch (ClassNotFoundException cnfe ) {
      logger.error("class not found");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }

    vertx.eventBus().request(address,
        request,
        reply -> {
          logger.info("Sending event to " + address);
          Object response = (Object)reply.result().body();
          ObjectMapper objectMapper = new ObjectMapper();
          try {
            context
                .response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(objectMapper.writeValueAsString(response));
          } catch (Exception e) {
            logger.error(e.getMessage());
          }

    });
  }

  private BasePojo createRequest(RoutingContext context) throws ClassNotFoundException {
    String[] pathComponents = Helpers.getPathComponents(context.request().path(), NAMESPACE);
    String address = Helpers.createEventbusAddress(
        pathComponents[0], pathComponents[1], pathComponents[2]);

    String pojoClass = Helpers.pojoMapper(address);
    logger.info("address \"" + address + "\" mapped to \"" + pojoClass + "\"");
    Class clazz = Class.forName(pojoClass);

    BasePojo request = (BasePojo)context.getBodyAsJson().mapTo(clazz);
    request.setCookieCrumb(cookieCrumbGenerator.next());
    logger.info("cookie crumb: " + request.getCookieCrumb());
    return request;
  }
}
