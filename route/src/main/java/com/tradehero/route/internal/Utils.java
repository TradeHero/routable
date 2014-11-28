package com.tradehero.route.internal;

final class Utils {
  private static final boolean DEBUG = true;

  static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  static void debug(String log) {
    if (DEBUG) {
      System.out.println(log);
    }
  }

  private Utils() {}
}
