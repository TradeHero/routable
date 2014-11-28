package com.tradehero.route;

import org.junit.Test;

import static org.truth0.Truth.ASSERT;

public class PathPatternTest {

  @Test
  public void testHas() throws Exception {
    PathPattern pathPattern = PathPattern.create(
        StaticPart.create("api"), DynamicPart.create("users", "[0-9]+"));
    ASSERT.that(pathPattern.has("users1")).isFalse();
    ASSERT.that(pathPattern.has("users"));
  }
}