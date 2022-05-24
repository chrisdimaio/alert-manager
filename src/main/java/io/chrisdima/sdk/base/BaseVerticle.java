package io.chrisdima.sdk.base;

import io.chrisdima.sdk.CookieCrumbGenerator;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.text.WordUtils;

public abstract class BaseVerticle extends AbstractVerticle {
  private String namespace = "internal";

  protected CookieCrumbGenerator cookieCrumbGenerator;
  protected Logger logger;

  protected BaseVerticle() {
    super();
    logger = LoggerFactory.getLogger( this.getClass() );

  }

  @Override
  public void start() throws Exception {
    processAddressAnnotations();
  }

  private Class<?> getPojoClassFromAddress(String address) {
    // Find a better way to do this.
    String pojoClass = WordUtils
        .capitalizeFully(address.replace(":", " "))
        .replace(" ", "");

    String pojoPath = this.getClass().getPackageName() + ".pojos." + pojoClass;
    try {
      return Class.forName(pojoPath);
    } catch (ClassNotFoundException e) {
      logger.error(e);
    }
    return null;
  }

  // Pull out all methods annotated with @Address and map them to their addresses.
  private void processAddressAnnotations() {
    for (Method method : this.getClass().getMethods()) {
      if (Objects.nonNull(method)) {
        Address addressAnnotation = method.getAnnotation(Address.class);
        if (Objects.nonNull(addressAnnotation)) {
          String address = addressAnnotation.value();
          String nameSpacedAddress = this.namespace + ":" + address;
          vertx.eventBus().consumer(nameSpacedAddress, message -> {
              try {
                Message<?> deserializedMessage =
                    new io.chrisdima.sdk.Message<>(message, getPojoClassFromAddress(address));
                method.invoke(this, deserializedMessage);
              } catch (IllegalAccessError | Exception e) {
                logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
//                message.fail(500, e.getMessage());
              }
          });
          this.publishService(nameSpacedAddress, nameSpacedAddress);
        }
      }
    }
  }

  protected Boolean serviceExists(String address) {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    AtomicReference<Boolean> exists = new AtomicReference<>(false);
    discovery.getRecord(r -> r.getLocation().getString("endpoint").equals(address), ar -> {
      if (ar.succeeded()) {
        if (ar.result() != null) {
          logger.info("Found service: " + ar.result());
          exists.set(true);
        } else {
          // the lookup succeeded, but no matching service
          logger.error("Failed to find service");
        }
      } else {
        // lookup failed
        logger.error("Error while looking up service");
      }
    });

    discovery.close();
    return exists.get();
  }

  protected void publishService(String name, String address) {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

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
}
