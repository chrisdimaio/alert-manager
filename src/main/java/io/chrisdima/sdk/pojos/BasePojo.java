package io.chrisdima.sdk.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.buffer.Buffer;

public abstract class BasePojo {
  @JsonIgnore
  private Boolean isBinary = false;

  @JsonIgnore
  private Buffer buffer;

  @JsonIgnore
  private String cookieCrumb;

  public Boolean isBinary() {
    return isBinary;
  }

  public void setIsBinary(Boolean isBinary) {
    this.isBinary = isBinary;
  }

  public Buffer getBuffer() {
    return buffer;
  }

  public void setBuffer(Buffer buffer) {
    this.buffer = buffer;
  }

  public String getCookieCrumb() {
    return cookieCrumb;
  }

  public void setCookieCrumb(String cookieCrumb) {
    this.cookieCrumb = cookieCrumb;
  }
}
