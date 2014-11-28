package com.tradehero.route;

import org.junit.Test;

import static org.truth0.Truth.ASSERT;

public class StaticPartTest {

  @Test
  public void testValue() throws Exception {
    StaticPart staticPart = StaticPart.create("api");
    ASSERT.that(staticPart.value()).isEqualTo("api");
  }
}