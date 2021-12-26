package io.chrisdima.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
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
import io.chrisdima.serviceregistry.ServiceRegistry;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class HTTPVerticle extends BaseVerticle {
  private static final int DEFAULT_HTTP_PORT = 1234;
  private final static String NAMESPACE = "internal";

//  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void start(Promise<Void> future) {
    logger = LoggerFactory.getLogger( APIHandler.class );

    // Publish this service.
    publishService(this.getClass().getName(), "some service");

    vertx.eventBus().registerDefaultCodec(UppercaseRequest.class, new UppercaseRequestCodec());
    vertx.eventBus().registerDefaultCodec(UppercaseResponse.class, new UppercaseResponseCodec());
    vertx.eventBus().registerDefaultCodec(LazyRequest.class, new LazyRequestCodec());
    vertx.eventBus().registerDefaultCodec(LazyResponse.class, new LazyResponseCodec());

    // Need to figure out cluster factory before this works. -cluster
    vertx.deployVerticle(new ServiceRegistry());
    vertx.deployVerticle(new UppercaseVerticle());
    vertx.deployVerticle(new LazyVerticle());



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
    String address = Helpers.createEventbusAddress(context, NAMESPACE);

    vertx.eventBus().request(address,
        request(context),
        reply -> reply(context, address, reply));
  }

  private void reply(RoutingContext context, String address, AsyncResult<Message<Object>> reply) {
    logger.info("Sending event to " + address);

    BasePojo response = (BasePojo)reply.result().body();
    respondToRestCall(context, response, "application/json; charset=utf-8");
  }

  private BasePojo request(RoutingContext context) {
    String address = Helpers.createEventbusAddress(context, NAMESPACE);

    Class<?> requestPojo = null;
    try {
      String pojoClass = Helpers.pojoMapper(address);
      logger.info("address \"" + address + "\" mapped to \"" + pojoClass + "\"");

      requestPojo = Class.forName(pojoClass);
    } catch (ClassNotFoundException e) {
      context.fail(HttpURLConnection.HTTP_NOT_FOUND);
      logger.error(e);
    }

    BasePojo request = (BasePojo)context.getBodyAsJson().mapTo(requestPojo);
    request.setCookieCrumb(cookieCrumbGenerator.next());
    logger.info("cookie crumb: " + request.getCookieCrumb());
    return request;
  }

  private void respondToRestCall(RoutingContext context, BasePojo response, String contentType) {
    try {
      if (response.isBinary()) {
        logger.info("It's binary!");
        context.response().putHeader("content-type", "image/jpeg").end(response.getBuffer());
      } else {
        context
            .response()
            .putHeader("content-type", contentType)
            .end(objectMapper.writeValueAsString(response));
      }
    }
    catch (JsonProcessingException jpe) {
      context.fail(HttpURLConnection.HTTP_INTERNAL_ERROR);
      logger.error(jpe);
    }
  }
}
