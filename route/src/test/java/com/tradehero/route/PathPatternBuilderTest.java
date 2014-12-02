package com.tradehero.route;

import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static java.util.Arrays.asList;

public class PathPatternBuilderTest {
  private static StaticPart _s(String value) {
    return StaticPart.create(value);
  }
  private static DynamicPart _d(String value) {
    return DynamicPart.create(value);
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

    expectResultExceptionWithSignatures("/foo/bar/{}", "must match", "Found {}");
    expectResultExceptionWithSignatures("/foo/{!!!}", "index 1 must match", "Found {!!!}");
    expectResultExceptionWithSignatures("/foo/{}/{taco}", "index 1 must match", "Found {}");
    // Invalid parameter, name cannot start with digit.
    expectResultExceptionWithSignatures("/foo/{1}", "index 1 must match", "Found {1}");
  }

  private void expectResultExceptionWithSignatures(String route, String... errorPieces) {
    try {
      PathPattern.builder(route).build();
      throw new RuntimeException("Operation should throw an error");
    } catch (Exception ex) {
      for (String errorPiece: errorPieces) {
        assert_().that(ex.getMessage()).contains(errorPiece);
      }
    }
  }

  private void expectPathParts(String route, PathPart... expected) {
    PathPattern calculated = PathPattern.builder(route).build();
    assert_().that(calculated).isNotNull();
    assert_().that(calculated.parts()).isEqualTo(asList(expected));
  }
}