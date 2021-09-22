package io.chrisdima.alertmanager.handlers;

import static io.chrisdima.alertmanager.data.SimpleDatastore.getAlert;


import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;

public class GetAlertHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext context) {
    String id = context.getBodyAsJson().getString("id");
    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(Map.of("id", getAlert(id))));
  }
}
