package com.tradehero.route.internal;

import com.tradehero.route.PathPattern;
import java.util.regex.Pattern;

class RoutableBinding {
  private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
  private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);
  private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");

  final PathPattern[] pathPatterns;

  private RoutableBinding(PathPattern[] pathPatterns) {
    this.pathPatterns = pathPatterns;
  }

  public static RoutableBinding parse(String[] routes) {
    PathPattern[] paths = new PathPattern[routes.length];
    for (int i = 0; i < routes.length; ++i) {
      paths[i] = parseRoute(routes[i]);
    }
    return new RoutableBinding(paths);
  }

  private static PathPattern parseRoute(String route) {
    // FIXME compile route to PathPattern
    return null;
  }
}
