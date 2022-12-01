package io.chrisdima.services;

import io.chrisdima.alertmanager.enums.ContactType;
import io.chrisdima.alertmanager.objects.Alert;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.utils.aws.AwsFactory;
import io.chrisdima.utils.aws.DynamoDBClient;
import io.chrisdima.utils.aws.SNS;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class AlertManager extends BaseVerticle {
  private DynamoDBClient dynamoDBClient;

  @Override
  public void run() {
    dynamoDBClient = AwsFactory.dynamoClient();
  }

  @Address("v1:call-alert")
  public void callAlert(Message<JsonObject> message) {
    if (message.body().containsKey("id")) {
      String id = message.body().getString("id");
      Alert alert = dynamoDBClient.getAlert(id);
      callAlert(alert);

      // Add error handling.
      message.reply(new JsonObject().put("success", true));
    }
  }

  @Address("v1:get-alert")
  public void getAlert(Message<JsonObject> message) {
    if (message.body().containsKey("id")) {
      String id = message.body().getString("id");
      message.reply(new JsonObject(Json.encode(dynamoDBClient.getAlert(id))));
    }
  }

  @Address("v1:put-alert")
  public void putAlert(Message<JsonObject> message) {
    JsonObject body = message.body();

    // We don't care if there's an id set already or not. We set our own so the alert can be
    // serialized.
    body.put("id", java.util.UUID.randomUUID().toString());
    Alert alert = body.mapTo(Alert.class);
    dynamoDBClient.putAlert(alert);
    message.reply(new JsonObject().put("id", alert.getId()));
  }

  private void callAlert(Alert alert) {
    alert.getContacts().stream()
        .filter(contact -> contact.getType().equals(ContactType.SMS))
        .forEach(sms -> SNS.sendSMS(alert.getMessage(), sms.getAddress()));
  }
}
