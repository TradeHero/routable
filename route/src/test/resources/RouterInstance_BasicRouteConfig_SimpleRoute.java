final class RouterInstance_BasicRouteConfig_SimpleRoute extends BasicRouteConfig.SimpleRoute {
  RouterInstance_BasicRouteConfig_SimpleRoute() {
    registerRoute(BasicRouteConfig$A$$Routable.PATH_PATTERNS, BasicRouteConfig.A.class);
  }

  @Override public void open(String url) {}
}