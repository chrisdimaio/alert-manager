package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Helpers;
import io.chrisdima.sdk.base.BaseVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public class JsonObjectVerticle extends BaseVerticle {
  private final static String EVENT = "jsonobject";
  private final static String VERSION = "v1";
  private final static String NAMESPACE = "internal";

  @Override
  public void start(Promise<Void> future) {
    logger = LoggerFactory.getLogger( JsonObjectVerticle.class );

    String address = Helpers.createEventbusAddress(EVENT, VERSION, NAMESPACE);
    publishService(this.getClass().getName(), address);

    logger.info("Listening to eventbus @ " + address);
    vertx.eventBus().consumer(address, message -> {
    JsonObject response = new JsonObject().put("base64", "aGVsbG8gYmFzZTY0");
    message.reply(response, new DeliveryOptions()
        .addHeader("content_type", "application/json; charset=utf-8")
        .addHeader("is_base64", String.valueOf(true)));
    });
  }
}
