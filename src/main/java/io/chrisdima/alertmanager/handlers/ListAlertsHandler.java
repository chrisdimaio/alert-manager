package io.chrisdima.alertmanager.handlers;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;

import static io.chrisdima.alertmanager.data.SimpleDatastore.getAllAlerts;

public class ListAlertsHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(getAllAlerts()));
  }
}
