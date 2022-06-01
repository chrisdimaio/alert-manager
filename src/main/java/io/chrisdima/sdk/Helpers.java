package io.chrisdima.sdk;

import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Helpers {
  private static final Map<String, String> addressToPOJO = Map.of(
      "internal:v1:uppercase", "io.chrisdima.sdk.pojos.UppercaseRequest",
      "internal:v1:image", "io.chrisdima.sdk.pojos.UppercaseRequest",
      "internal:v1:jsonobject", "io.vertx.core.json.JsonObject",
      "internal:v1:lazy", "io.chrisdima.sdk.pojos.LazyRequest"
  );

//  Maps address components to a pojo class.
  public static String pojoMapper(String address) throws ClassNotFoundException {
    if (addressToPOJO.containsKey(address)) {
      return addressToPOJO.get(address);
    }
    throw new ClassNotFoundException();
  }

  public static Properties getProjectProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(Helpers.class.getClassLoader().getResourceAsStream("project.properties"));
    return properties;
  }

  public static String createEventbusAddress(RoutingContext context, String namespace) {
    String[] components = context.request().uri().split("/");

    String event = null;
    String version = null;

    // If the URI contains 3 components use the provided namespace if it contains more than 3 then
    // pull the namespace from the URI.
    if(components.length == 3) {
      version = components[1];
      event = components[2];
    } else if(components.length > 3){
      namespace = components[1];
      version = components[2];
      event = components[3];
    }
    return String.format("%s:%s:%s", namespace, version, event);
  }

  public static String createEventbusAddress(String event, String version, String namespace) {
    return String.format("%s:%s:%s", namespace, version, event);
  }
}
