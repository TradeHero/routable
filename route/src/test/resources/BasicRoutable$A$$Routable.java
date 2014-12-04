// Generated code by Route. Do not modify!

import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;

public final class BasicRoutable$A$$Routable {
  public static PathPattern[] PATH_PATTERNS = {
      PathPattern.create(StaticPart.create("api"), DynamicPart.create("a", "Int", null))
  };

  public static void inject(final BasicRoutable.A target, Bundle source) {
    Bundle subBundle = source.getBundle("BasicRoutable.A");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("a")) {
      target.a = source.getInt("a");
    }
  }

  public static void save(final BasicRoutable.A source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putInt("a", source.a);

    if (!flat) dest.putBundle("BasicRoutable.A", toWrite);
  }
}