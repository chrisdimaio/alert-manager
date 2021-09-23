package io.chrisdima.alertmanager.handlers;

import io.chrisdima.utils.aws.AwsFactory;
import io.chrisdima.utils.aws.DynamoDBClient;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class GetAlertHandler implements Handler<RoutingContext> {
  private final DynamoDBClient dynamoDBClient;
  private final Logger logger = LoggerFactory.getLogger( DynamoDBClient.class );

  public GetAlertHandler() {
    dynamoDBClient = AwsFactory.dynamoClient();
  }
  @Override
  public void handle(RoutingContext context) {
    String id = context.getBodyAsJson().getString("id");
    context
        .response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(dynamoDBClient.getAlert(id)));
  }
}
