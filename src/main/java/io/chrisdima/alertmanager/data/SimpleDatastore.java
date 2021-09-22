package io.chrisdima.alertmanager.data;

import io.chrisdima.alertmanager.objects.Alert;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDatastore {
  private static final AtomicInteger COUNTER = new AtomicInteger();
  private static final Map<String, Alert> DATA = new HashMap<>();

  public static String putAlert(Alert alert) {
    String id = String.valueOf(COUNTER.getAndIncrement());
    DATA.put(id, alert);
    return id;
  }

  public static Alert getAlert(String id) {
    return DATA.get(id);
  }

  public static Map<String, Alert>  getAllAlerts() {
    return DATA;
  }
}
