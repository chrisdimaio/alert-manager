package io.chrisdima.services;

import io.chrisdima.http.HTTPVerticle;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.chrisdima.sdk.examples.FileGetterVerticle;
import io.chrisdima.sdk.examples.JsonObjectVerticle;
import io.chrisdima.sdk.examples.pojos.V1Filegetter;
import io.chrisdima.services.pojos.V1DeployService;
import io.chrisdima.services.pojos.V1Services;
import io.chrisdima.services.pojos.V1UndeployService;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstService extends BaseVerticle {

  private static final String V1_SERVICE = "v1:services";
  private static final String V1_TEST = "v1:test";

  @Override
  public void run() {
    vertx.deployVerticle(new JsonObjectVerticle()
        .withNamespace(System.getenv("NAMESPACE")));
    vertx.deployVerticle(new FileGetterVerticle()
        .withNamespace(System.getenv("NAMESPACE")));
    vertx.deployVerticle(new HTTPVerticle()
        .withNamespace(System.getenv("NAMESPACE")));
  }

  @Address(V1_TEST)
  public void v1Test(Message<JsonObject> message) {
    // It's better to check the HTTP method that made the request.
    if (method(message).equals(HttpMethod.GET)) {
      logger.info("Handling HTTP GET request");

      JsonObject test = message.body();
      message.reply(new JsonObject().put("found", test));
    } else if (method(message).equals(HttpMethod.POST)) {
      logger.info("Handling HTTP POST request");

      String test = message.body().getString("test");
      message.reply(new JsonObject().put("found", test));
    }
  }

  @Address("v1:deploy-service")
  public void deployService(Message<V1DeployService> message) {
    try {
      Class<?> clazz = Class.forName(message.body().className);
      Constructor<?> constructor = clazz.getConstructor();
      BaseVerticle verticle = (BaseVerticle) constructor.newInstance();
      message.body().namespace.ifPresent(verticle::setNamespace);

      vertx.deployVerticle(verticle)
          .onComplete(stringAsyncResult -> {
        if (stringAsyncResult.succeeded()) {
          message.reply(new JsonObject().put("deploymentID", stringAsyncResult.result()));
        } else {
          logger.error(stringAsyncResult.cause().getMessage() + "\n"
              + Arrays.toString(stringAsyncResult.cause().getStackTrace()));
          message.fail(500, stringAsyncResult.cause().getMessage());
        }
      });

    } catch (ClassNotFoundException | NoSuchMethodException e) {
      logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
      message.fail(400, e.getMessage());
    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
      logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
      message.fail(500, e.getMessage());
    }

  }

  @Address("v1:undeploy-service")
  public void undeployService(Message<V1UndeployService> message) {
    if (vertx.deploymentIDs().contains(message.body().deploymentID)) {
      vertx.undeploy(message.body().deploymentID).onComplete(stringAsyncResult -> {
        if (stringAsyncResult.succeeded()) {
          message.reply(new JsonObject().put("success", true));
        } else {
          logger.error(stringAsyncResult.cause().getMessage() + "\n"
              + Arrays.toString(stringAsyncResult.cause().getStackTrace()));
          message.fail(500, stringAsyncResult.cause().getMessage());
        }
      });
    } else {
      message.fail(404, "Deployment not found");
    }
  }

  @Address(V1_SERVICE)
  public void services(Message<V1Services> message) {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    discovery.getRecords(r -> true, ar -> {
      if (ar.succeeded()) {
        List<JsonObject> services = new ArrayList<>();
        ar.result().forEach(record -> services.add(record.toJson()));
        message.reply(new JsonObject().put("services", services));
      } else {
        message.fail(500, "Listing services error");
      }
    });

    discovery.close();
  }
}
