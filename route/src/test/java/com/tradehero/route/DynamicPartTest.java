package com.tradehero.route;

import org.junit.Test;

import static com.google.common.truth.Truth.assert_;

public class DynamicPartTest {
  @Test
  public void testName() {
    DynamicPart dynamicPart = DynamicPart.create("groups", "Int", "[0-9]+");
    assert_().that(dynamicPart.name()).isEqualTo("groups");
  }
}