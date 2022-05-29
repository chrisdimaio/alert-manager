package io.chrisdima.http;

import io.chrisdima.sdk.Constants;
import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class HTTPVerticle extends BaseVerticle {
  private static final int DEFAULT_HTTP_PORT = 1234;

  @Override
  public void run() {
    Router router = Router.router(vertx);
    router.route("/*").handler(BodyHandler.create().setBodyLimit(Constants.BODY_SIZE_LIMIT));
    router.route().handler(this::handler);

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(
        config().getInteger("http.port", DEFAULT_HTTP_PORT),
        result -> {
          if (result.succeeded()) {
            logger.info("HTTP service started successfully");
          }
          if (result.failed()) {
            logger.error("HTTP service failed to start: " + result.cause() + "\n"
                + Arrays.toString(result.cause().getStackTrace()));
          }
        });
  }

  public void handler(RoutingContext context) {
    String address = Helpers.createEventbusAddress(context, getNamespace());

    if (serviceExists(address)) {

      JsonObject message;
      HttpMethod requestMethod = context.request().method();
      if (requestMethod.equals(HttpMethod.GET)) {
        message = new JsonObject()
            .put("params", convertMultiMapToCollections(context.request().params()));
      } else {
        message = context.body().asJsonObject();
      }

      logger.info("Sending request to: " + address);
      vertx.eventBus().request(
          address,
          message,
          new DeliveryOptions().addHeader("METHOD", String.valueOf(requestMethod)),
          reply -> reply(context, reply)
      );
    } else {
      logger.error(address + " not found!");
      context.response().setStatusCode(404).end();
    }
  }

  public List<Map<String, String>> convertMultiMapToCollections(MultiMap multiMap) {
    List<Map<String, String>> list = new ArrayList<>();
    multiMap.entries().forEach(entry -> list.add(Map.of(entry.getKey(), entry.getValue())));
    return list;
  }

  private void reply(RoutingContext context, AsyncResult<Message<Object>> reply) {
    if (reply.succeeded()) {

      context.response().putHeader("content-type", reply.result().headers().get("content_type"));
      boolean isBase64 = Boolean.parseBoolean(reply.result().headers().get("is_base64"));

      if (isBase64) {
        String base64 = ((JsonObject) reply.result().body()).getString("base64");
        context.response().end(Buffer.buffer(Base64.getDecoder().decode(base64)));

      } else {
        context.response().end(((JsonObject) reply.result().body()).encode());
      }
    } else {
      ReplyException exception = (ReplyException) reply.cause();
      logger.info("code: " + exception.failureCode());
      context.response().setStatusCode(exception.failureCode()).end();
    }
  }
}
