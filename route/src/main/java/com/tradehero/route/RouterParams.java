package com.tradehero.route;

import java.util.Map;

class RouterParams {
  RouterOptions routerOptions;
  Map<String, String> openParams;

  public RouterParams(Map<String, String> givenParams, RouterOptions routerOptions) {
    this.openParams = givenParams;
    this.routerOptions = routerOptions;
  }
}