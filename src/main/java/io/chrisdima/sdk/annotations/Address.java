package io.chrisdima.sdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that attaches a method an address on the event-bus.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Address {

  /**
   * Holds the address on the event-bus the annotated method should be assigned to.
   *
   * @return address
   */
  String value();
}