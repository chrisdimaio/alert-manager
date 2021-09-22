package io.chrisdima.alertmanager;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DeploymentOptions options = new DeploymentOptions().setInstances(4);
    System.out.println(options.toJson());
  }
}
