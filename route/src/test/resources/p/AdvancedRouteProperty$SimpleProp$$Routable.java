package p;

import android.os.Bundle;
import com.tradehero.route.Router;

public final class AdvancedRouteProperty$SimpleProp$$Routable {
  public static void inject(final p.AdvancedRouteProperty.SimpleProp target, Bundle source) {
    Bundle subBundle = source.getBundle("p.AdvancedRouteProperty.SimpleProp");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("a")) {
      target.a = source.getString("a");
    }
  }

  public static void save(final p.AdvancedRouteProperty.SimpleProp source, Bundle dest,
      boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putString("a", source.a);
    if (!flat) dest.putBundle("p.AdvancedRouteProperty.SimpleProp", toWrite);
  }
}