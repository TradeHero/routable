package com.tradehero.route;

import com.google.auto.value.AutoValue;
import com.sun.istack.internal.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A complete path pattern, consisting of a sequence of path parts.
 */
@AutoValue
public abstract class PathPattern {
  public static PathPattern create(PathPart... parts) {
    return new AutoValue_PathPattern(Arrays.asList(parts));
  }

  public static PathPattern create(List<PathPart> parts) {
    return new AutoValue_PathPattern(Collections.unmodifiableList(parts));
  }

  /** Unmodifiable list of path parts */
  public abstract List<PathPart> parts();

  /**
   * Whether this path pattern has a parameter by the given name.
   */
  public final boolean has(@NotNull String key) {
    for (PathPart partPath: parts()) {
      if (partPath instanceof DynamicPart) {
        if (key.equals(((DynamicPart) partPath).name())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder("PathPattern: ");
    for (PathPart pathPart: parts()) {
      sb.append('/');
      if (pathPart instanceof DynamicPart) {
        sb.append(((DynamicPart) pathPart).name());
      } else if (pathPart instanceof StaticPart) {
        sb.append(((StaticPart) pathPart).value());
      }
    }
    return sb.toString();
  }
}
