package com.tradehero.route.internal;

import com.google.common.collect.ImmutableMap;
import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;
import java.util.Map;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.tradehero.route.internal.BundleType.INT;
import static com.tradehero.route.internal.BundleType.INT_ARRAY;
import static com.tradehero.route.internal.BundleType.STRING;
import static java.util.Arrays.asList;

public class PathPatternBuilderTest {
  private static StaticPart _s(String value) {
    return StaticPart.create(value);
  }
  private static DynamicPart _d(String value) {
    return DynamicPart.create(value, STRING.type, null);
  }
  private static DynamicPart _d(String value, String bundleType) {
    return DynamicPart.create(value, bundleType, null);
  }

  @Test public void pathParameterParsing() throws Exception {
    expectPathParts("/");
    expectPathParts("/foo", _s("foo"));
    expectPathParts("/foo/bar", _s("foo"), _s("bar"));
    expectPathParts("/foo/bar/{taco}", _s("foo"), _s("bar"), _d("taco"));
    expectPathParts("/foo/{t}", _s("foo"), _d("t"));
    expectPathParts("/foo/{taco}/or/{burrito}", _s("foo"), _d("taco"), _s("or"), _d("burrito"));
    expectPathParts("/foo/{taco-shell}", _s("foo"), _d("taco-shell"));
    expectPathParts("/foo/{taco_shell}", _s("foo"), _d("taco_shell"));
    expectPathParts("/foo/{sha256}", _s("foo"), _d("sha256"));
    expectPathParts("/foo/{TACO}", _s("foo"), _d("TACO"));
    expectPathParts("/foo/{taco}/{tAco}/{taCo}", _s("foo"), _d("taco"), _d("tAco"), _d("taCo"));

    expectResultExceptionWithSignatures("/foo/bar/{}", "index 2 must match", "Found {}");
    expectResultExceptionWithSignatures("/foo/{!!!}", "index 1 must match", "Found {!!!}");
    expectResultExceptionWithSignatures("/foo/{}/{taco}", "index 1 must match", "Found {}");
    // Invalid parameter, name cannot start with digit.
    expectResultExceptionWithSignatures("/foo/{1}", "index 1 must match", "Found {1}");

    Map<String, BundleType> typeRepo = ImmutableMap.of("t", INT, "users", INT_ARRAY);
    expectPathParts("/foo/{t}", typeRepo, _s("foo"), _d("t", "Int"));
    expectPathParts("/foo/{t}/bar/{users}/and/{sha256}", typeRepo,
        _s("foo"), _d("t", "Int"),
        _s("bar"), _d("users", "IntArray"),
        _s("and"), _d("sha256", "String"));
  }

  private void expectResultExceptionWithSignatures(String route, String... errorPieces) {
    try {
      new PathPatternBuilder(route).build();
      throw new RuntimeException("Operation should throw an error");
    } catch (Exception ex) {
      for (String errorPiece: errorPieces) {
        assert_().that(ex.getMessage()).contains(errorPiece);
      }
    }
  }

  private void expectPathParts(String route, PathPart... expected) {
    expectPathParts(route, null, expected);
  }

  private void expectPathParts(String route, Map<String, BundleType> typeMap, PathPart... expected) {
    PathPattern calculated = new PathPatternBuilder(route)
        .typeMap(typeMap)
        .build();
    assert_().that(calculated).isNotNull();
    assert_().that(calculated.parts()).isEqualTo(asList(expected));
  }
}