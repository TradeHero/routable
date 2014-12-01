package com.tradehero.route.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

final class Utils {
  private static final boolean DEBUG = true;

  static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  static String stackTraceToString(Throwable e) {
    StringWriter stackTrace = new StringWriter();
    e.printStackTrace(new PrintWriter(stackTrace));
    return stackTrace.toString();
  }

  static void debug(String log) {
    if (DEBUG) {
      System.out.println(log);
    }
  }

  private Utils() {}
}
