package com.tradehero.route;

import com.google.auto.value.AutoValue;
import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.TypeElement;

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

  public static Builder builder(String route) {
    return new Builder(route);
  }

  public static class Builder {
    private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    private static final String EASY_PARAM = "[^}]*";
    private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);
    private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + EASY_PARAM + ")\\}");
    private static final String PATH_DELIM = "/";

    private final String route;
    private Map<String, TypeElement> typeMap;

    public Builder(String route) {
      this.route = route;
    }

    public Builder typeMap(Map<String, TypeElement> typeMap) {
      this.typeMap = typeMap;
      return this;
    }

    public PathPattern build() {
      ensureSaneDefaults();
      return parseRoute(route);
    }

    private void ensureSaneDefaults() {
      if (!route.startsWith(PATH_DELIM)) {
        throw new IllegalStateException("Route must started with " + PATH_DELIM);
      }

      if (typeMap == null) {
        typeMap = Collections.emptyMap();
      }
    }

    private PathPattern parseRoute(String route) {
      int question = route.indexOf('?');
      String url = route;
      String query = null;
      if (question != -1 && question < route.length() - 1) {
        url = route.substring(0, question);
        query = route.substring(question + 1);
      }

      /** TODO parse query */
      return parseUrl(url);
    }

    private PathPattern parseUrl(String url) {
      Matcher m = PARAM_URL_REGEX.matcher(url);

      int position = 0;
      List<PathPart> paths = new LinkedList<PathPart>();
      while (m.find()) {
        int matchStart = m.start();
        paths.addAll(parseStaticUrl(url.substring(position, matchStart)));
        position = m.end();

        String name = m.group(1);
        validateDynamicPartName(paths.size(), name);
        paths.add(DynamicPart.create(name));
      }
      paths.addAll(parseStaticUrl(url.substring(position, url.length())));
      return PathPattern.create(paths);
    }

    private void validateDynamicPartName(int index, String name) {
      if (!PARAM_NAME_REGEX.matcher(name).matches()) {
        throw error("Path part with index %d must match \\{%s\\}. Found {%s} in %s",
            index, PARAM_NAME_REGEX.pattern(), name, route);
      }
    }

    private List<PathPart> parseStaticUrl(String staticUrl) {
      int firstDelimPos = staticUrl.indexOf(PATH_DELIM);
      if (firstDelimPos < 0) {
        return Collections.emptyList();
      }
      staticUrl = staticUrl.substring(firstDelimPos + 1);
      if (staticUrl.length() == 0) {
        return Collections.emptyList();
      }

      int lastDelimPos = staticUrl.lastIndexOf(PATH_DELIM);
      if (lastDelimPos < 0) {
        return Collections.singletonList((PathPart) StaticPart.create(staticUrl));
      }

      String[] staticParts = staticUrl.split(PATH_DELIM);
      List<PathPart> pathParts = new ArrayList<PathPart>(staticParts.length);
      for (String part: staticParts) {
        pathParts.add(StaticPart.create(part));
      }
      return pathParts;
    }

    private RuntimeException error(String message, Object... args) {
      if (args.length > 0) {
        message = String.format(message, args);
      }
      return new IllegalArgumentException(message);
    }
  }
}
