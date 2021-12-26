package io.chrisdima.serviceregistry;

import io.chrisdima.sdk.examples.UppercaseVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;

public class ServiceRegistry extends AbstractVerticle {
  private static final String SERVICE_REGISTRY_MAP = "service_registry";
  private static final String REGISTER_SERVICE_ADDRESS = "service_registry:register:v1";
  private static final String UNREGISTER_SERVICE_ADDRESS = "service_registry:unregister:v1";
  private static final String LIST_SERVICES_ADDRESS = "service_registry:list_services:v1";

  private final Logger logger = LoggerFactory.getLogger( ServiceRegistry.class );

  private LocalMap<String, String> serviceRegistry;

  @Override
  public void start(Promise<Void> future) {
    serviceRegistry = vertx.sharedData().getLocalMap(SERVICE_REGISTRY_MAP);

    vertx.eventBus().consumer(REGISTER_SERVICE_ADDRESS, request -> {
      String service = (String)request.body();

      // Currently we only need the key which is the service address. This may change as more info
      // is needed.
      serviceRegistry.put(service, service);
      logger.info("Registering: " + service);
      logger.info(serviceRegistry);
    });

    vertx.eventBus().consumer(UNREGISTER_SERVICE_ADDRESS, request -> {
      String service = (String)request.body();

      if (serviceRegistry.containsKey(service)){
        serviceRegistry.remove(service);
        logger.info("Unregistering: " + service);
      } else {
        logger.error("Service \"" + service + "\" not registered");
      }
      logger.info(serviceRegistry);
    });

    vertx.eventBus().consumer(LIST_SERVICES_ADDRESS, request -> {
      String service = (String)request.body();
      request.reply(serviceRegistry.keySet().toString());
      logger.info(serviceRegistry);
    });
  }
}
