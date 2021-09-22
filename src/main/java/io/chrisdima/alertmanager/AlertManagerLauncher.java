package io.chrisdima.alertmanager;

import io.chrisdima.alertmanager.enums.ContactType;
import io.chrisdima.alertmanager.objects.Alert;
import io.chrisdima.alertmanager.objects.Contact;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import java.util.List;
import java.util.Map;


import static io.chrisdima.alertmanager.data.SimpleDatastore.putAlert;

public class AlertManagerLauncher extends Launcher {
  private final Logger logger = LoggerFactory.getLogger( AlertManagerLauncher.class );

  public static void main(String[] args) {
    new AlertManagerLauncher().dispatch(args);
  }

  @Override
  public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
    if(deploymentOptions.getConfig().containsKey("instances")) {
      super.beforeDeployingVerticle(
          deploymentOptions
              .setInstances(deploymentOptions.getConfig().getInteger("instances")));
    }
    logger.info("Deployment Options: " + Json.encodePrettily(deploymentOptions.toJson()));
  }

}
