package io.chrisdima.sdk;

import java.util.Random;

public class CookieCrumbGenerator {
  private final Random random;

  public CookieCrumbGenerator() {
    this.random = new Random();
  }

  public String next() {
    return Integer.toHexString(this.random.nextInt()).toUpperCase();
  }
}
