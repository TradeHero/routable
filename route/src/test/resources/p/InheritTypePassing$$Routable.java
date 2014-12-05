// Generated code by Route. Do not modify!
package p;

import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;

public final class InheritTypePassing$$Routable {
  public static PathPattern[] PATH_PATTERNS = {
      PathPattern.create(StaticPart.create("api"), DynamicPart.create("doubleFoo", "Double", null), DynamicPart.create("intBar", "Int", null), DynamicPart.create("shortTaco", "Short", null))};

  public static void inject(final p.InheritTypePassing target, Bundle source) {
    Bundle subBundle = source.getBundle("p.InheritTypePassing");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("doubleFoo")) {
      target.doubleFoo = source.getDouble("doubleFoo");
    }
    if (target.prop == null) target.prop = new p.InheritTypePassing.Prop();
    Router.getInstance().inject(target.prop, source);
  }

  public static void save(final p.InheritTypePassing source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putDouble("doubleFoo", source.doubleFoo);
    Router.getInstance().saveSingle(toWrite, source.prop, flat);

    if (!flat) dest.putBundle("p.InheritTypePassing", toWrite);
  }
}