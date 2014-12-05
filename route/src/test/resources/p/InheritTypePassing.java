package p;

import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

@Routable("/api/{doubleFoo}/{intBar}/{shortTaco}")
public class InheritTypePassing {
  @RouteProperty Double doubleFoo;
  @RouteProperty Prop prop;

  @RouteProperty("propBundleKey")
  public static class Prop {
    @RouteProperty Integer intBar;
    @RouteProperty Short shortTaco;
  }
}