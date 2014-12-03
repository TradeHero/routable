package com.tradehero.route.integration;

import com.google.common.base.Joiner;
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
    JavaFileObject sourceFile = JavaFileObjects.forSourceString("Basic", Joiner.on("\n").join(
        "import com.tradehero.route.Routable;",
        "import com.tradehero.route.RouteProperty;",
        "class Basic {",
        "  @Routable(\"/api/{a}\")",
        "  static class A {",
        "    @RouteProperty Integer a;",
        "  }",
        "}"
    ));

    JavaFileObject generatedSource = JavaFileObjects.forSourceString("Basic$A$$Routable",
        Joiner.on("\n").join(
            "import android.os.Bundle;",
            "import com.tradehero.route.Router;",
            "import com.tradehero.route.DynamicPart;",
            "import com.tradehero.route.PathPattern;",
            "import com.tradehero.route.StaticPart;",
            "public final class Basic$A$$Routable {",
            "  public static PathPattern[] PATH_PATTERNS = {",
            "   PathPattern.create(",
            "     StaticPart.create(\"api\"),",
            "     DynamicPart.create(\"a\", \"Int\", null)",
            "   )",
            "  };",
            "  public static void inject(final Basic.A target, Bundle source) {",
            "    Bundle subBundle = source.getBundle(\"Basic.A\");",
            "    if (subBundle != null) {",
            "      inject(target, subBundle);",
            "    }",
            "    if (source.containsKey(\"a\")) {",
            "      target.a = source.getInt(\"a\");",
            "    }",
            "  }",
            "  public static void save(final Basic.A source, Bundle dest, boolean flat) {",
            "    Bundle toWrite = null;",
            "    toWrite = flat ? dest : new Bundle();",
            "    toWrite.putInt(\"a\", source.a);",
            "    if (!flat) dest.putBundle(\"Basic.A\", toWrite);",
            "  }",
            "}"));

    assert_().about(javaSource())
        .that(sourceFile)
        .processedWith(new RouterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedSource);
  }

  @Test public void verifyTraversalRoutableTree() {

    JavaFileObject sourceFile = JavaFileObjects.forSourceString("Basic", Joiner.on("\n").join(
        "import com.tradehero.route.Routable;",
        "import com.tradehero.route.RouteProperty;",
        "class Basic {",
        "  @Routable(\"/api/{a}\")",
        "  static class A extends B {",
        "    Integer key;",
        "  }",
        "  static class B extends C {",
        "    Integer keyB;",
        "  }",
        "  @Routable(\"/api2/{c}\")",
        "  static class C {",
        "    @RouteProperty Integer keyC;",
        "  }",
        "  @Routable(\"/api3/{d}\")",
        "  static class D extends C {",
        "    Integer keyD;",
        "  }",
        "}"
    ));

    assert_().about(javaSource()).that(sourceFile).processedWith(new RouterProcessor())
        .compilesWithoutError();
  }
}
