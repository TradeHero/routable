package com.tradehero.route.sample.key;

import com.tradehero.route.RouteProperty;

@RouteProperty("user")
public class UserBaseKey {
  private int userId;

  public UserBaseKey() {}

  public UserBaseKey(int userId) {
    this.userId = userId;
  }

  @RouteProperty
  public int getUserId() {
    return userId;
  }

  @RouteProperty
  public void setUserId(int userId) {
    this.userId = userId;
  }
}
