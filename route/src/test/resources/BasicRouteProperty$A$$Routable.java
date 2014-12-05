// Generated code by Route. Do not modify!
import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.Router.Injector;

public class BasicRouteProperty$A$$Routable<T extends BasicRouteProperty.A> implements Injector<T> {
  @Override public void inject(final T target, Bundle source) {
    Bundle subBundle = source.getBundle("BasicRouteProperty.A");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("a")) {
      target.a = source.getString("a");
    }
    if (source.containsKey("b")) {
      target.setB(source.getInt("b"));
    }
  }

  @Override public void save(final T source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putString("a", source.a);

    if (!flat) dest.putBundle("BasicRouteProperty.A", toWrite);
  }
}