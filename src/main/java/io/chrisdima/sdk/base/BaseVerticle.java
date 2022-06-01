package io.chrisdima.sdk.base;

import io.chrisdima.sdk.Constants;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseVerticle extends AbstractVerticle {
  private final List<Record> serviceRecords = new ArrayList<>();
  protected String namespace;
  protected Logger logger;

  protected BaseVerticle() {
    super();
    logger = LoggerFactory.getLogger( this.getClass() );

    if (System.getenv().containsKey("NAMESPACE")) {
      this.namespace = System.getenv("NAMESPACE");
    } else {
      this.namespace = Constants.DEFAULT_NAMESPACE;
    }
  }

  @Override
  public void start() {
    processAddressAnnotations();
    run();
  }

  @Override
  public void stop() {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    serviceRecords.forEach(record -> discovery.unpublish(record.getRegistration(), ar -> {
      if (ar.succeeded()) {
        logger.info("Successfully un-published: " + record.getLocation());
      } else {
        logger.info("Unable to un-published: " + record.getLocation());
      }
    }));
  }

  // Runs at the end of start()
  public void run() {}

  public String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(String namespace) {
    if (Objects.nonNull(namespace)) {
      this.namespace = namespace;
    } else {
      this.namespace = Constants.DEFAULT_NAMESPACE;
    }
  }

  public BaseVerticle withNamespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  private Class<?> getPojoClassFromMethod(Method method) {
    Type actualType = null;
    if (method.getGenericParameterTypes().length == 1) {
      ParameterizedType parameterizedType =
          (ParameterizedType) method.getGenericParameterTypes()[0];
      if (parameterizedType.getActualTypeArguments().length == 1) {
        actualType = parameterizedType.getActualTypeArguments()[0];
        logger.info("Actual type: " + actualType);
      }
    }

    try {
      return Class.forName(Objects.requireNonNull(actualType).getTypeName());
    } catch (ClassNotFoundException e) {
      logger.error(e);
    }
    return JsonObject.class;
  }

  // Pull out all methods annotated with @Address and map them to their addresses.
  private void processAddressAnnotations() {
    for (Method method : this.getClass().getMethods()) {
      if (Objects.nonNull(method)) {
        Address addressAnnotation = method.getAnnotation(Address.class);
        if (Objects.nonNull(addressAnnotation)) {
          String address = addressAnnotation.value();
          String nameSpacedAddress = namespace + ":" + address;
          createConsumer(nameSpacedAddress, method);
          this.publishService(nameSpacedAddress, nameSpacedAddress);
        }
      }
    }
  }

  private <T> void createConsumer (String nameSpacedAddress, Method method) {
    vertx.eventBus().consumer(nameSpacedAddress,
        (io.vertx.core.eventbus.Message<T>message) -> {
          try {
            Message<?> deserializedMessage =
                    new io.chrisdima.sdk.Message<>(message, getPojoClassFromMethod(method));
            method.invoke(this, deserializedMessage);
          } catch (IllegalAccessError | IllegalAccessException | InvocationTargetException e) {
            logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
            message.fail(500, e.getMessage());
          } catch (IllegalArgumentException e) {
            logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
            message.fail(400, e.getMessage());
          }
        });
  }

  protected Boolean serviceExists(String address) {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    AtomicReference<Boolean> exists = new AtomicReference<>(false);
    discovery.getRecord(r -> r.getLocation().getString("address").equals(address), ar -> {
      if (ar.succeeded()) {
        if (ar.result() != null) {
          logger.debug("Found service: " + ar.result().getLocation().getString("address"));
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
        .setType("eventbus-service")
        .setLocation(new JsonObject().put("address", address))
        .setName(name)
        .setMetadata(new JsonObject()
            .put("className", this.getClass())
            .put("deploymentID", vertx.getOrCreateContext().deploymentID())
        .put("namespace", namespace));

    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        // Save the service record, so we can un-publish later if need be.
        serviceRecords.add(ar.result());

        logger.info("Successfully published service: "
            + ar.result().getLocation().getString("address"));
      } else {
        logger.error("Failed to publish service: " + record);
      }
    });

    discovery.close();
  }

  protected HttpMethod method(Message<?> message) {
    return HttpMethod.valueOf(message.headers().get("METHOD"));
  }
}
