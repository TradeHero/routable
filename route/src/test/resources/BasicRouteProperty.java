import com.tradehero.route.RouteProperty;

class BasicRouteProperty {
  static class A {
    Integer key;
    @RouteProperty String a;

    @RouteProperty void setB(Integer key) {
      this.key = key;
    }
  }
}