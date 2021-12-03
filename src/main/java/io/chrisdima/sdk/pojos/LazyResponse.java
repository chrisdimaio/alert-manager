package io.chrisdima.sdk.pojos;

public class LazyResponse extends BasePojo {
  private String message = "I'm lazy...";

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
