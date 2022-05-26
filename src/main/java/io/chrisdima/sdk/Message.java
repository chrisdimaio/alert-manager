package io.chrisdima.sdk;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import java.util.Objects;

public class Message<T> implements io.vertx.core.eventbus.Message<Object> {
  private final io.vertx.core.eventbus.Message<?> message;
  private final T body;

  public Message(io.vertx.core.eventbus.Message<?> message, Class<T> clazz)
      throws IllegalArgumentException {
    this.message = message;

    JsonObject body = (JsonObject) message.body();
    if (clazz.equals(JsonObject.class)) {
      // There needs to be a better way to do this. Without it deserialization fails with below
      // error.
      // ava.lang.IllegalArgumentException: Unrecognized field "test"
      // (class io.vertx.core.json.JsonObject), not marked as ignorable (one known property: "map"])
      body = new JsonObject().put("map", body);
    }
    this.body = body.mapTo(clazz);
  }

  @Override
  public String address() {
    return this.message.address();
  }

  @Override
  public MultiMap headers() {
    return this.message.headers();
  }

  @Override
  public T body() {
    return this.body;
  }

  @Override
  public @Nullable String replyAddress() {
    return this.message.replyAddress();
  }

  @Override
  public boolean isSend() {
    return this.message.isSend();
  }

  @Override
  public void reply(@Nullable Object o, DeliveryOptions deliveryOptions) {
    this.message.reply(o, deliveryOptions);
  }

  @Override
  public <R> Future<io.vertx.core.eventbus.Message<R>>  replyAndRequest(@Nullable Object o,
                                                                        DeliveryOptions deliveryOptions) {
    return this.message.replyAndRequest(o, deliveryOptions);
  }
}
