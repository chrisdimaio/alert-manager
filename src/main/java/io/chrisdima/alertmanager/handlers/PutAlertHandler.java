package io.chrisdima.alertmanager.handlers;

import static io.chrisdima.utils.aws.DynamoDB.putAlert;


import io.chrisdima.alertmanager.objects.Alert;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;

public class PutAlertHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext context) {
    JsonObject jsonObject = context.getBodyAsJson();

    // We don't care if there's an id set all ready, we set it to our own and so the alert can be
    // serialized.
    jsonObject.put("id", java.util.UUID.randomUUID().toString());
    Alert alert = jsonObject.mapTo(Alert.class);
    putAlert(alert);
    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(Map.of("id", alert.getId())));
  }
}
