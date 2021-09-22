package io.chrisdima.alertmanager.handlers;

import io.chrisdima.alertmanager.enums.ContactType;
import io.chrisdima.alertmanager.objects.Alert;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.chrisdima.utils.auth.SuperBasicTokenCheck;
import io.chrisdima.utils.aws.SNS;


import static io.chrisdima.alertmanager.data.SimpleDatastore.getAlert;

public class CallAlertHandler implements Handler<RoutingContext> {
  private final Logger logger = LoggerFactory.getLogger( CallAlertHandler.class );
  @Override
  public void handle(RoutingContext context) {
    String id = context.getBodyAsJson().getString("id");
    if(SuperBasicTokenCheck.checkAuth(context)) {
      Alert alert = getAlert(id);
      callAlert(alert);
      context
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(alert));
    }
  }

  private void callAlert(Alert alert) {
    alert.getContacts().stream()
        .filter(contact -> contact.getType().equals(ContactType.SMS))
        .forEach(sms -> SNS.sendSMS(alert.getMessage(), sms.getAddress()));
  }
}
