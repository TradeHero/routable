package com.tradehero.route.integration;

import com.google.testing.compile.JavaFileObjects;
import com.tradehero.route.internal.RouterProcessor;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by tho on 22/07/2014.
 */
@RunWith(JUnit4.class)
public class RoutePropertyGenerationTest {
  @Test public void basicRoutePropertyGeneration() {
    JavaFileObject sourceFile = JavaFileObjects.forResource("BasicRouteProperty.java");

    JavaFileObject generatedSource =
        JavaFileObjects.forResource("BasicRouteProperty$A$$Routable.java");

    assert_().about(javaSource())
        .that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError().and()
        .generatesSources(generatedSource);
  }

  @Test public void advancedRoutePropertyGeneration() {
    JavaFileObject sourceFile = JavaFileObjects.forResource("p/AdvancedRouteProperty.java");
    JavaFileObject generatedASource = JavaFileObjects
        .forResource("p/AdvancedRouteProperty$A$$Routable.java");
    JavaFileObject generatedSimplePropSource = JavaFileObjects
        .forResource("p/AdvancedRouteProperty$SimpleProp$$Routable.java");

    assert_().about(javaSource())
        .that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedASource, generatedSimplePropSource);
  }
}
