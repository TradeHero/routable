package com.tradehero.route.internal;

import com.tradehero.route.PathPattern;
import java.util.Map;

/** TODO Do we really need RoutableBinding, or we can just use PathPattern for RouteInjector */
class RoutableBinding {
  final PathPattern[] pathPatterns;

  private RoutableBinding(PathPattern[] pathPatterns) {
    this.pathPatterns = pathPatterns;
  }

  /**
   * Parse routes from String form to PathPattern.
   * @param routes routes in String form
   * @param typeMap map between variable name and type
   * @return RoutableBinding
   */
  public static RoutableBinding parse(String[] routes, Map<String, BundleType> typeMap) {
    PathPattern[] paths = new PathPattern[routes.length];
    for (int i = 0; i < routes.length; ++i) {
      paths[i] = new PathPatternBuilder(routes[i])
          .typeMap(typeMap)
          .build();
    }
    return new RoutableBinding(paths);
  }
}
