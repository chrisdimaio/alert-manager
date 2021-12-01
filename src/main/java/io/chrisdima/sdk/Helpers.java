package io.chrisdima.sdk;

import com.google.inject.internal.util.Maps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Helpers {
  private static final Map<String, String> addressToPOJO = Map.of(
      "hello", "world",
      "internal:v1:crazy_case", "io.chrisdima.sdk.pojos.UppercaseRequest"
  );

//  Maps address components to a pojo class.
  public static String pojoMapper(String event, String version, String namespace) {
    String address = createEventbusAddress(event, version, namespace);
    return addressToPOJO.getOrDefault(address, null);
  }

  public static String createEventbusAddress(String event, String version, String namespace) {
    return String.format("%s:%s:%s", namespace, version, event);
  }

  public static String[] getPathComponents(String path, String namespace) {
    String[] components = path.split("/");

    String event = null;
    String version = null;
    if(components.length >= 3){
      version = components[1];
      event = components[2];
    }
    return new String[] {event, version, namespace};
  }
}
