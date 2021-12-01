package io.chrisdima.sdk.examples;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;

public class UppercaseLauncher extends Launcher {
  private final Logger logger = LoggerFactory.getLogger( UppercaseLauncher.class );

  public static void main(String[] args) {
    new UppercaseLauncher().dispatch(args);
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
