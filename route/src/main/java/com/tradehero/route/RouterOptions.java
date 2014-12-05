package com.tradehero.route;

import android.app.Activity;

/**
 * The class used to determine behavior when opening a URL. If you want to extend Routable to handle
 * things like transition animations or
 * fragments, this class should be augmented.
 */
public class RouterOptions {
  Class<? extends Activity> klass;
  RouterCallback callback;

  public RouterOptions() {
  }

  public RouterOptions(Class<? extends Activity> klass) {
    this.klass = klass;
  }

  public RouterCallback getCallback() {
    return this.callback;
  }
}
