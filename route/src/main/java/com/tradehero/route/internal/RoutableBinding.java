package com.tradehero.route.internal;

import com.tradehero.route.PathPattern;
import java.util.Map;
import javax.lang.model.element.TypeElement;

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
  public static RoutableBinding parse(String[] routes, Map<String, TypeElement> typeMap) {
    PathPattern[] paths = new PathPattern[routes.length];
    for (int i = 0; i < routes.length; ++i) {
      paths[i] = PathPattern.builder(routes[i])
          .typeMap(typeMap)
          .build();
    }
    return new RoutableBinding(paths);
  }
}
