package p;

import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.Router.Injector;

public class AdvancedRouteProperty$SimpleProp$$Routable<T extends p.AdvancedRouteProperty.SimpleProp> implements Injector<T> {
  @Override public void inject(final T target, Bundle source) {
    Bundle subBundle = source.getBundle("p.AdvancedRouteProperty.SimpleProp");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("a")) {
      target.a = source.getString("a");
    }
  }

  @Override public void save(final T source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putString("a", source.a);

    if (!flat) dest.putBundle("p.AdvancedRouteProperty.SimpleProp", toWrite);
  }
}