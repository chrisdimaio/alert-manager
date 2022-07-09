package io.chrisdima.services;

import static io.chrisdima.sdk.Constants.DEFAULT_ID_COUNT;
import static io.chrisdima.sdk.Constants.DEFAULT_ID_SIZE;
import static io.chrisdima.sdk.enums.IdType.RANDOM_INTEGER;


import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.enums.IdType;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.stream.IntStream;

public class IdServer extends BaseVerticle {

  @Address("v1:generate-id")
  public void generateId(Message<JsonObject> message) {
    int size = message.body().getInteger("size", DEFAULT_ID_SIZE);
    IdType type = IdType.valueOf(message.body().getString("type"));
    message.reply(new JsonObject().put("id", getId(size, type)));
  }

  @Address("v1:bulk-generate-id")
  public void bulkGenerateId(Message<JsonObject> message) {
    int count = message.body().getInteger("count", DEFAULT_ID_COUNT);
    int size = message.body().getInteger("size", DEFAULT_ID_SIZE);
    IdType type = IdType.valueOf(message.body().getString("type", RANDOM_INTEGER.name()));

    ArrayList<Object> ids = new ArrayList<>();

    IntStream.range(0, count).forEachOrdered(i -> ids.add(i, getId(size, type)));

    message.reply(new JsonObject().put("ids", ids));
  }

  private Object getId(int size, IdType type) {
    switch (type) {
      case RANDOM_ALPHANUMERIC -> {
        return randomAlphanumeric(size);
      }
      case RANDOM_INTEGER -> {
        return randomBigInt(size);
      }
      case RANDOM_STRING -> {
        return randomString(size);
      }
      case SORTABLE_BIG_INT -> {
        // This is basic. Code from IdServer (Snowflake imp) needs to be ported.
        return System.nanoTime();
      }
      default -> {
        return 1;
      }
    }
  }

  private String randomAlphanumeric(int size) {
    return randomBigInt(size).toString(36).toUpperCase();
  }

  private BigInteger randomBigInt(int size) {
    return new BigInteger(size, new Random());
  }

  private String randomString(int size) {
    byte[] randomBytes = new byte[size];
    new Random().nextBytes(randomBytes);
    return new String(Base64.getEncoder().encode(randomBytes));
  }
}
