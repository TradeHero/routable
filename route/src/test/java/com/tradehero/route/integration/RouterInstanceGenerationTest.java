package com.tradehero.route.integration;

import com.google.testing.compile.JavaFileObjects;
import com.tradehero.route.internal.RouterProcessor;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class RouterInstanceGenerationTest {
  @Test public void basicRouteConfigGenerationTest() {
    JavaFileObject sourceFile = JavaFileObjects.forResource("BasicRouteConfig.java");
    JavaFileObject generateSource1 = JavaFileObjects
        .forResource("RouterInstance_BasicRouteConfig_SimpleRoute.java");

    assert_().about(javaSource())
        .that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generateSource1);
  }
}
