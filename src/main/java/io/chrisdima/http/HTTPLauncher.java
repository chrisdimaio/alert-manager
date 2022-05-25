package io.chrisdima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;

public class HTTPLauncher extends Launcher {
  private final Logger logger = LoggerFactory.getLogger( HTTPLauncher.class );

  public static void main(String[] args) {
    io.vertx.core.json.jackson.DatabindCodec codec = (io.vertx.core.json.jackson.DatabindCodec) io.vertx.core.json.Json.CODEC;
    ObjectMapper mapper = codec.mapper();
    mapper.registerModule(new Jdk8Module());
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
