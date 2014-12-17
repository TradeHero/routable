package com.tradehero.route.sample;

import com.tradehero.route.Router;
import com.tradehero.route.RouterInstance;

@RouterInstance
public abstract class TestRouter extends Router {
  public static TestRouter create() {
    return new RouterInstance_TestRouter();
  }
}
