// Generated code by Route. Do not modify!
package p;

import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.Router.Injector;

public class AdvancedRouteProperty$A$$Routable<T extends p.AdvancedRouteProperty.A> implements Injector<T> {
  @Override public void inject(final T target, Bundle source) {
    Bundle subBundle = source.getBundle("p.AdvancedRouteProperty.A");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (target.a == null) target.a = new p.AdvancedRouteProperty.SimpleProp();
    Router.getInstance().inject(target.a, source);
  }

  @Override public void save(final T source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    Router.getInstance().saveSingle(toWrite, source.a, flat);

    if (!flat) dest.putBundle("p.AdvancedRouteProperty.A", toWrite);
  }
}