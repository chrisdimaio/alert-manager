package io.chrisdima.sdk.base;

import io.chrisdima.sdk.CookieCrumbGenerator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public abstract class BaseVerticle extends AbstractVerticle {
  protected CookieCrumbGenerator cookieCrumbGenerator;
  protected Logger logger;

  protected BaseVerticle() {
    super();
    this.cookieCrumbGenerator = new CookieCrumbGenerator();
  }

  protected String discoverService(String name) {
    return "nada";
//    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
//
//    // Customize the configuration
//    discovery = ServiceDiscovery.create(vertx,
//        new ServiceDiscoveryOptions()
//            .setAnnounceAddress("service-announce")
//            .setName("my-name"));
//
//    // Get a record by name
//    discovery.getRecord(r -> r.getName().equals(this.getClass().getName()), ar -> {
//      if (ar.succeeded()) {
//        if (ar.result() != null) {
//          logger.info("Found service: " + ar.result());
//          return ar.result().getLocation();
//        } else {
//          // the lookup succeeded, but no matching service
//          logger.error("Failed to find service");
//        }
//      } else {
//        // lookup failed
//      }
//    });
  }

  protected void publishService(String name, String address){
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    // Customize the configuration
    discovery = ServiceDiscovery.create(vertx,
        new ServiceDiscoveryOptions()
            .setAnnounceAddress("service-announce")
            .setName("my-name"));

    Record record = new Record()
        .setType("eventbus-service-proxy")
        .setLocation(new JsonObject().put("endpoint", address))
        .setName(name)
        .setMetadata(new JsonObject().put("some-label", "some-value"));

    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        // publication succeeded
        logger.info("Successfully published service: " + ar.result());
      } else {
        // publication failed
        logger.error("Failed to publish service: " + record);
      }
    });

    discovery.close();
  }

//  Need to implement this in a way that pulls out the reply object's cookie crumb and reports it.
//  protected  <T> EventBus request(String address, @Nullable Object message, Handler<AsyncResult<Message<T>>> replyHandler) {
//    return vertx.eventBus().request(address, message, new DeliveryOptions(), replyHandler);
//  }
}
