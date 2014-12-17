import com.tradehero.route.Routable;
import com.tradehero.route.Router;
import com.tradehero.route.RouterInstance;
import com.tradehero.route.RouteProperty;

class BasicRouteConfig {
  @Routable("/api/{a}")
  static class A {
    @RouteProperty Integer a;
  }

  @RouterInstance()
  static abstract class SimpleRoute extends Router {
    public static SimpleRoute create() {
      return new RouterInstance_BasicRouteConfig_SimpleRoute();
    }
  }
}