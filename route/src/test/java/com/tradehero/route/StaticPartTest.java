package com.tradehero.route;

import org.junit.Test;

import static com.google.common.truth.Truth.assert_;

public class StaticPartTest {

  @Test
  public void testValue() throws Exception {
    StaticPart staticPart = StaticPart.create("api");
    assert_().that(staticPart.value()).isEqualTo("api");
  }
}