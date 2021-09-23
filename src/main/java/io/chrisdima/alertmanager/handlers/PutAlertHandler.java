package io.chrisdima.alertmanager.handlers;

import io.chrisdima.alertmanager.objects.Alert;
import io.chrisdima.utils.aws.AwsFactory;
import io.chrisdima.utils.aws.DynamoDBClient;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;

public class PutAlertHandler implements Handler<RoutingContext> {
  private final DynamoDBClient dynamoDBClient;

  public PutAlertHandler() {
    dynamoDBClient = AwsFactory.dynamoClient();
  }

  @Override
  public void handle(RoutingContext context) {
    JsonObject jsonObject = context.getBodyAsJson();

    // We don't care if there's an id set already or not. We set our own so the alert can be
    // serialized.
    jsonObject.put("id", java.util.UUID.randomUUID().toString());
    Alert alert = jsonObject.mapTo(Alert.class);
    dynamoDBClient.putAlert(alert);
    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(Map.of("id", alert.getId())));
  }
}
