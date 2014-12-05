// Generated code by Route. Do not modify!
package p;

import android.os.Bundle;
import com.tradehero.route.Router;

public final class InheritTypePassing$Prop$$Routable {
  public static void inject(final p.InheritTypePassing.Prop target, Bundle source) {
    Bundle subBundle = source.getBundle("propBundleKey");
    if (subBundle != null) {
      inject(target, subBundle);
    }
    if (source.containsKey("intBar")) {
      target.intBar = source.getInt("intBar");
    }
    if (source.containsKey("shortTaco")) {
      target.shortTaco = source.getShort("shortTaco");
    }
  }

  public static void save(final p.InheritTypePassing.Prop source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putInt("intBar", source.intBar);
    toWrite.putShort("shortTaco", source.shortTaco);

    if (!flat) dest.putBundle("propBundleKey", toWrite);
  }
}