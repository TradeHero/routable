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
        "    @RouteProperty String a;",
        "  }",
        "}"
    ));

    JavaFileObject generatedSource = JavaFileObjects.forSourceString("Basic$A$$Routable",
        Joiner.on("\n").join(
            "import android.os.Bundle;",
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
}
