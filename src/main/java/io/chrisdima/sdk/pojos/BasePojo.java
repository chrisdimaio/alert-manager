package io.chrisdima.sdk.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BasePojo {
  @JsonIgnore
  private String cookieCrumb;

  public String getCookieCrumb() {
    return cookieCrumb;
  }

  public void setCookieCrumb(String cookieCrumb) {
    this.cookieCrumb = cookieCrumb;
  }
}
