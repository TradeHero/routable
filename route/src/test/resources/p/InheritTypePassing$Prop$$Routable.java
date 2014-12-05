// Generated code by Route. Do not modify!
package p;

import android.os.Bundle;
import com.tradehero.route.Router;
import com.tradehero.route.Router.Injector;

public class InheritTypePassing$Prop$$Routable<T extends p.InheritTypePassing.Prop> implements Injector<T> {
  @Override public void inject(final T target, Bundle source) {
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

  @Override public void save(final T source, Bundle dest, boolean flat) {
    Bundle toWrite = null;
    toWrite = flat ? dest : new Bundle();
    toWrite.putInt("intBar", source.intBar);
    toWrite.putShort("shortTaco", source.shortTaco);

    if (!flat) dest.putBundle("propBundleKey", toWrite);
  }
}