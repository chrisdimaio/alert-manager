package io.chrisdima.alertmanager;

import io.chrisdima.alertmanager.handlers.CallAlertHandler;
import io.chrisdima.alertmanager.handlers.GetAlertHandler;
import io.chrisdima.alertmanager.handlers.ListAlertsHandler;
import io.chrisdima.alertmanager.handlers.PutAlertHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class AlertManagerVerticle extends AbstractVerticle {
  private static final int DEFAULT_HTTP_PORT = 8081;

  @Override
  public void start(Promise<Void> future) {
    Router router = Router.router(vertx);
    router.route("/*").handler(BodyHandler.create());
    router.get("/list").handler(new ListAlertsHandler());
    router.post("/call_alert").handler(new CallAlertHandler());
    router.post("/put_alert").handler(new PutAlertHandler());
    router.post("/get_alert").handler(new GetAlertHandler());

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(
            config().getInteger("http.port", DEFAULT_HTTP_PORT),
            result -> {
              if (result.succeeded()) {
                future.complete();
              } else {
                future.fail(result.cause());
              }
            }
        );
  }
}
