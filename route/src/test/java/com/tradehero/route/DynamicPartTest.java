package com.tradehero.route;

import org.junit.Test;

import static org.truth0.Truth.ASSERT;

public class DynamicPartTest {
  @Test
  public void testName() {
    DynamicPart dynamicPart = DynamicPart.create("groups", "[0-9]+");
    ASSERT.that(dynamicPart.name()).isEqualTo("groups");
  }
}