package p;

import com.tradehero.route.RouteProperty;

class AdvancedRouteProperty {
  static class SimpleProp {
    @RouteProperty String a;
  }

  static class A {
    @RouteProperty SimpleProp a;
  }
}