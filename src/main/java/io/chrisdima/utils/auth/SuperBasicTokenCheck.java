package io.chrisdima.utils.auth;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class SuperBasicTokenCheck {
  private static final Logger logger = LoggerFactory.getLogger( SuperBasicTokenCheck.class );

  private static final String BEARER_FIELD = "Bearer ";
  private static final String AUTH_HEADER = "Authorization";

  public static boolean checkAuth(RoutingContext context) {
    if(!context.request().headers().contains(AUTH_HEADER)) {
      logger.info("No Authorization header");
      return false;
    }
    String authHeather = context.request().headers().get(AUTH_HEADER);
    if(!authHeather.startsWith(BEARER_FIELD)) {
      logger.info("Bad Authorization header");
      return false;
    }
    return authHeather
        .substring(BEARER_FIELD.length()).equals(System.getenv("API_TOKEN"));
  }
}
