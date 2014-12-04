import android.os.Bundle;
import com.tradehero.route.Router;

public final class BasicRouteProperty$A$$Routable {
  public static void inject(final BasicRouteProperty.A target, Bundle source) {
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

  public static void save(final BasicRouteProperty.A source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putString("a", source.a);
    if (!flat) dest.putBundle("BasicRouteProperty.A", toWrite);
  }
}