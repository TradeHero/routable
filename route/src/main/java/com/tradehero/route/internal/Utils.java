package com.tradehero.route.internal;

public final class Utils {
  private static final boolean DEBUG = true;

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  public static void debug(String log) {
    if (DEBUG) {
      System.out.println(log);
    }
  }

  private Utils() {}
}
