package io.chrisdima.sdk.base;

import io.chrisdima.sdk.CookieCrumbGenerator;
import io.vertx.core.AbstractVerticle;

public abstract class BaseVerticle extends AbstractVerticle {
  protected CookieCrumbGenerator cookieCrumbGenerator;

  protected BaseVerticle() {
    super();
    this.cookieCrumbGenerator = new CookieCrumbGenerator();
  }

//  Need to implement this in a way that pulls out the reply object's cookie crumb and reports it.
//  protected  <T> EventBus request(String address, @Nullable Object message, Handler<AsyncResult<Message<T>>> replyHandler) {
//    return vertx.eventBus().request(address, message, new DeliveryOptions(), replyHandler);
//  }
}
