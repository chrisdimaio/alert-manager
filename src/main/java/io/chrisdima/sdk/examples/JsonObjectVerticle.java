package io.chrisdima.sdk.examples;

import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.examples.pojos.V1Deserialize;
import io.vertx.core.json.JsonObject;

public class JsonObjectVerticle extends BaseVerticle {

  @Address("v1:deserialize")
  public void deserialize(Message<V1Deserialize> message) {
    logger.info("chris: " + message.body());
    message.reply(new JsonObject().put("deserialized", "stuff"));
  }
}
