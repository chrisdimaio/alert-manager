package io.chrisdima.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.examples.FileGetterVerticle;
import io.chrisdima.sdk.examples.JsonObjectVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.net.HttpURLConnection;
import java.util.Base64;

public class HTTPVerticle extends BaseVerticle {
  private static final int DEFAULT_HTTP_PORT = 1234;
  private final static String NAMESPACE = "internal";

//  private final Logger logger = LoggerFactory.getLogger( APIHandler.class );
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void start(Promise<Void> future) {
    logger = LoggerFactory.getLogger( HTTPVerticle.class );

    // Publish this service.
    publishService(this.getClass().getName(), "some service");

    vertx.deployVerticle(new JsonObjectVerticle());
    vertx.deployVerticle(new FileGetterVerticle());



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

    if (serviceExists(address)) {
      vertx.eventBus().request(address,
          context.getBodyAsJson(),
          reply -> reply(context, reply));
    } else {
      logger.error(address + " not found!");
      context.response().setStatusCode(404).end();
    }
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
