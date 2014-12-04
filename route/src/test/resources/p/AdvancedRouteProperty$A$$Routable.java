package p;

import android.os.Bundle;
import com.tradehero.route.Router;

public final class AdvancedRouteProperty$A$$Routable {
  public static void inject(final p.AdvancedRouteProperty.A target, Bundle source) {
    Bundle subBundle = source.getBundle("p.AdvancedRouteProperty.A");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (target.a == null) target.a = new p.AdvancedRouteProperty.SimpleProp();
    Router.getInstance().inject(target.a, source);
  }

  public static void save(final p.AdvancedRouteProperty.A source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    Router.getInstance().saveSingle(toWrite, source.a, flat);
    if (!flat) dest.putBundle("p.AdvancedRouteProperty.A", toWrite);
  }
}