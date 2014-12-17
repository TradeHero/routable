package com.tradehero.route;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.tradehero.route.internal.RouterProcessor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Router {
  private static final String TAG = "Router";
  private static boolean debug = true;

  public interface Injector<T> {
    void inject(final T target, Bundle source);
    void save(final T source, Bundle dest, boolean flat);
  }

  public static abstract class RoutableInjector<T> implements Injector<T> {
    public PathPattern[] pathPatterns() { return null; }
  }

  static final Map<Class<?>, Injector<?>> INJECTORS = new LinkedHashMap<Class<?>, Injector<?>>();

  static final Injector<?> NO_OP = null;

  /**
   * Creates a new Router
   */
  protected Router() {
  }

  /**
   *
   * @param url The URL; for example, "users/16" or "groups/5/topics/20"
   */
  public abstract void open(String url);

  public void inject(Activity activity) {
    Bundle extras = activity.getIntent() != null ? activity.getIntent().getExtras() : null;
    inject(activity, extras);
  }

  public void inject(Object target, Bundle extras) {
    Class<?> targetClass = target.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up injector for " + targetClass.getName());
      @SuppressWarnings("unchecked")
      Injector<Object> injector = (Injector<Object>) findInjectorForClass(targetClass);
      if (injector != null) {
        injector.inject(target, extras);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to inject for " + target, e);
    }
  }

  /** Save POJO to bundle with hierarchy */
  public void save(Bundle extras, Object... parcelables) {
    if (parcelables == null) return;

    boolean flat = false;
    int length = parcelables.length;

    if (length > 1 && parcelables[length - 1] instanceof Boolean) {
      flat = (Boolean) parcelables[length - 1];
      --length;
    }

    for (int i = 0; i< length;  ++i) {
      saveSingle(extras, parcelables[i], flat);
    }
  }

  /** Save POJO to bundle without hierarchy */
  public void saveFlat(Bundle extras, Object... parcelables) {
    for (Object parcelable: parcelables) {
      saveSingle(extras, parcelable, true);
    }
  }

  public void saveSingle(Bundle extras, Object parcelable, boolean flat) {
    Class<?> targetClass = parcelable.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up saver for " + targetClass.getName());
      @SuppressWarnings("unchecked")
      Injector<Object> inject = (Injector<Object>) findInjectorForClass(targetClass);
      if (inject != null) {
        inject.save(parcelable, extras, flat);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to inject for " + parcelable, e);
    }
  }

  private Injector<?> findInjectorForClass(Class<?> cls)
      throws NoSuchMethodException, IllegalAccessException, InstantiationException,
      InvocationTargetException {
    Injector<?> injector = INJECTORS.get(cls);
    if (injector != null) {
      if (debug) Log.d(TAG, "HIT: Cached in injector map.");
      return injector;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NO_OP;
    }
    try {
      injector = (Injector<?>) Class.forName(clsName + RouterProcessor.SUFFIX)
          .getDeclaredConstructor(Router.class)
          .newInstance(this);
      if (debug) Log.d(TAG, "HIT: Class loaded injection class.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      injector = findInjectorForClass(cls.getSuperclass());
    }
    INJECTORS.put(cls, injector);
    return injector;
  }
}
