import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import com.tradehero.route.internal.RouterProcessor;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;
/**
 * Created by tho on 22/07/2014.
 */
@RunWith(JUnit4.class)
public class RoutePropertyGenerationTest {
  @Test public void basicRoutePropertyGeneration() {
    JavaFileObject sourceFile = JavaFileObjects.forSourceString("Basic", Joiner.on("\n").join(
        "import com.tradehero.route.RouteProperty;",
        "class Basic {",
        "  static class A {",
        "    Integer key;",
        "    @RouteProperty String a;",
        "    @RouteProperty void setB(Integer key) { this.key = key; } ",
        "  }",
        "}"
    ));

    JavaFileObject generatedSource = JavaFileObjects.forSourceString("Basic$A$$Routable",
        Joiner.on("\n").join(
            "import android.os.Bundle;",
            "import com.tradehero.route.Router;",
            "public final class Basic$A$$Routable {",
            "  public static void inject(final Basic.A target, Bundle source) {",
            "    Bundle subBundle = source.getBundle(\"Basic.A\");",
            "    if (subBundle != null) {",
            "      inject(target, subBundle);",
            "    }",
            "    ",
            "    if (source.containsKey(\"a\")) {",
            "      target.a = source.getString(\"a\");",
            "    }",
            "    if (source.containsKey(\"b\")) {",
            "      target.setB(source.getInt(\"b\"));",
            "    }",
            "  }",
            "  public static void save(final Basic.A source, Bundle dest, boolean flat) {",
            "    Bundle toWrite = null;",
            "    toWrite = flat ? dest : new Bundle();",
            "    toWrite.putString(\"a\", source.a);",
            "    if (!flat) dest.putBundle(\"Basic.A\", toWrite);",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(sourceFile).processedWith(new RouterProcessor())
        .compilesWithoutError().and()
        .generatesSources(generatedSource);
  }

  @Test public void basicInjectRouteGeneration() {
    JavaFileObject sourceFile = JavaFileObjects.forSourceString("p.Advanced", Joiner.on("\n").join(
        "package p;",
        "import com.tradehero.route.RouteProperty;",
        "class Advanced {",
        "  static class SimpleProp {",
        "    @RouteProperty String a;",
        "  }",
        "  static class A {",
        "    @RouteProperty SimpleProp a;",
        "  }",
        "}"
    ));

    JavaFileObject generatedASource = JavaFileObjects
        .forSourceString("p/Advanced$A$$Routable", Joiner.on("\n").join(
            "package p;",
            "import android.os.Bundle;",
            "import com.tradehero.route.Router;",
            "public final class Advanced$A$$Routable {",
            "  public static void inject(final p.Advanced.A target, Bundle source) {",
            "    Bundle subBundle = source.getBundle(\"p.Advanced.A\");",
            "    if (subBundle != null) {",
            "      inject(target, subBundle);",
            "    }",
            "    if (target.a == null) target.a = new p.Advanced.SimpleProp();",
            "    Router.getInstance().inject(target.a, source);",
            "  }",
            "  public static void save(final p.Advanced.A source, Bundle dest, boolean flat) {",
            "    Bundle toWrite = null;",
            "    toWrite = flat ? dest : new Bundle();",
            "    Router.getInstance().saveSingle(toWrite, source.a, flat);",
            "    if (!flat) dest.putBundle(\"p.Advanced.A\", toWrite);",
            "  }",
            "}"));

    JavaFileObject generatedSimplePropSource = JavaFileObjects
        .forSourceString("p/Advanced$SimpleProp$$Routable", Joiner.on("\n").join(
            "package p;",
            "import android.os.Bundle;",
            "import com.tradehero.route.Router;",
            "public final class Advanced$SimpleProp$$Routable {",
            "  public static void inject(final p.Advanced.SimpleProp target, Bundle source) {",
            "    Bundle subBundle = source.getBundle(\"p.Advanced.SimpleProp\");",
            "    if (subBundle != null) {",
            "      inject(target, subBundle);",
            "    }",
            "    if (source.containsKey(\"a\")) {",
            "      target.a = source.getString(\"a\");",
            "    }",
            "  }",

            "  public static void save(final p.Advanced.SimpleProp source, Bundle dest, boolean flat) {",
            "    Bundle toWrite = null;",
            "    toWrite = flat ? dest : new Bundle();",
            "    toWrite.putString(\"a\", source.a);",
            "    if (!flat) dest.putBundle(\"p.Advanced.SimpleProp\", toWrite);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(sourceFile).processedWith(new RouterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedASource, generatedSimplePropSource);
  }
}
