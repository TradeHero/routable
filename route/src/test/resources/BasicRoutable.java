import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

class BasicRoutable {
  @Routable("/api/{a}")
  static class A {
    @RouteProperty Integer a;
  }
}