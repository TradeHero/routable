import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;
import com.tradehero.route.Router.RoutableInjector;

public class BasicRoutable$A$$Routable<T extends BasicRoutable.A> extends RoutableInjector<T> {
  public static PathPattern[] PATH_PATTERNS = {
      PathPattern.create(StaticPart.create("api"), DynamicPart.create("a", "Int", null))
  };

  @Override public PathPattern[] pathPatterns() {
    return PATH_PATTERNS;
  }

  @Override public void inject(final T target, Bundle source) {
    Bundle subBundle = source.getBundle("BasicRoutable.A");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("a")) {
      target.a = source.getInt("a");
    }
  }

  @Override public void save(final T source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putInt("a", source.a);

    if (!flat) dest.putBundle("BasicRoutable.A", toWrite);
  }
}