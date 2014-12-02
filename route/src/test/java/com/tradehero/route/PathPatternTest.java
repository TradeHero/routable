package com.tradehero.route;

import org.junit.Test;

import static com.google.common.truth.Truth.assert_;

public class PathPatternTest {

  @Test
  public void testHas() throws Exception {
    PathPattern pathPattern = PathPattern.create(
        StaticPart.create("api"), DynamicPart.create("users", "[0-9]+"));
    assert_().that(pathPattern.has("users1")).isFalse();
    assert_().that(pathPattern.has("users"));
  }
}