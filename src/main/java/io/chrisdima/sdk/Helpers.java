package io.chrisdima.sdk;

import io.vertx.ext.web.RoutingContext;
import java.util.Map;

public class Helpers {
  private static final Map<String, String> addressToPOJO = Map.of(
      "internal:v1:uppercase", "io.chrisdima.sdk.pojos.UppercaseRequest",
      "internal:v1:image", "io.chrisdima.sdk.pojos.UppercaseRequest",
      "internal:v1:lazy", "io.chrisdima.sdk.pojos.LazyRequest"
  );

//  Maps address components to a pojo class.
  public static String pojoMapper(String address) throws ClassNotFoundException {
    if (addressToPOJO.containsKey(address)) {
      return addressToPOJO.get(address);
    }
    throw new ClassNotFoundException();
  }

  public static String createEventbusAddress(RoutingContext context, String namespace) {
    String[] components = context.request().path().split("/");

    String event = null;
    String version = null;
    if(components.length >= 3){
      version = components[1];
      event = components[2];
    }
    return String.format("%s:%s:%s", namespace, version, event);
  }

  public static String createEventbusAddress(String event, String version, String namespace) {
    return String.format("%s:%s:%s", namespace, version, event);
  }
}
