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
public class RoutableGenerationTest {
  @Test public void basicRoutableGeneration() {
    JavaFileObject sourceFile = JavaFileObjects.forResource("BasicRoutable.java");
    JavaFileObject generatedSource = JavaFileObjects.forResource("BasicRoutable$A$$Routable.java");

    assert_().about(javaSource()).that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedSource);
  }

  @Test public void verifyTraversalRoutableTree() {
    JavaFileObject sourceFile = JavaFileObjects.forResource("TraversalRoutableTree.java");
    assert_().about(javaSource()).that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError();
  }
}
