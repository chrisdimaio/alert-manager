package io.chrisdima.http;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import java.util.concurrent.TimeUnit;

public class HTTPLauncher extends Launcher {
  private final Logger logger = LoggerFactory.getLogger( HTTPLauncher.class );

  public static void main(String[] args) {
    new HTTPLauncher().dispatch(args);
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
