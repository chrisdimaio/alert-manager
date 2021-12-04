package io.chrisdima.sdk;

public class SDKMain {
  public static void main(String[] args) {
    CookieCrumbGenerator a = new CookieCrumbGenerator();
    for (int i = 0; i < 100000000; i++) {
      System.out.println(a.next());
    }
    System.out.println("no collisions!");
  }
}
