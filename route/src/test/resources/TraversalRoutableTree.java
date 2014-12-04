import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

class TraversalRoutableTree {
  @Routable("/api/{a}")
  static class A extends B {
    Integer key;
  }

  static class B extends C {
    Integer keyB;
  }

  @Routable("/api2/{c}")
  static class C {
    @RouteProperty Integer keyC;
  }

  @Routable("/api3/{d}")
  static class D extends C {
    Integer keyD;
  }
}