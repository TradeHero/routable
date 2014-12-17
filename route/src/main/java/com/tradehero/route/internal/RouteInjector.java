package com.tradehero.route.internal;

import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;
import java.util.LinkedHashSet;
import java.util.Set;

final class RouteInjector {
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private final ClassBinding ownBinding;
  private final Set<PathPatternBuilder> pathPatternBuilders = new LinkedHashSet<PathPatternBuilder>();
  private String parentInjector;

  RouteInjector(String classPackage, String className, String targetClass, String bundleKey) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.ownBinding = new ClassBinding(bundleKey);
  }

  String getFqcn() {
    return (Utils.isNullOrEmpty(classPackage) ? "" : classPackage + ".") + className;
  }

  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code by Route. Do not modify!\n");
    if (!Utils.isNullOrEmpty(classPackage)) {
      builder.append("package ").append(classPackage).append(";\n\n");
    }
    builder.append("import android.os.Bundle;\n");
    builder.append("import com.tradehero.route.Router;\n");

    if (pathPatternBuilders.size() > 0) {
      builder.append("import com.tradehero.route.DynamicPart;\n");
      builder.append("import com.tradehero.route.PathPattern;\n");
      builder.append("import com.tradehero.route.StaticPart;\n");
      builder.append("import com.tradehero.route.Router.RoutableInjector;\n");
    } else {
      builder.append("import com.tradehero.route.Router.Injector;\n");
    }
    builder.append("\n");

    // class content
    String inheritance = (pathPatternBuilders.size() > 0 ? " extends Routable" : " implements ")
        + "Injector<T>";
    builder.append("public class ")
        .append(className)
        .append("<T extends ").append(targetClass).append(">")
        .append(inheritance)
        .append(" {\n");
    if (pathPatternBuilders.size() > 0) {
      emitRoutes(builder);
      builder.append("\n\n");
    }

    emitConstructor(builder);

    if (ownBinding.bindings().size() > 0) {
      emitInject(builder);
      builder.append("\n\n");

      emitSaver(builder);
      builder.append("\n");
    } else {
      throw new IllegalStateException("Class " + targetClass + " has no injection point");
    }

    builder.append("}\n");

    return builder.toString();
  }

  private void emitConstructor(StringBuilder builder) {
    builder.append("  ")
        .append("private final Router router;\n\n");

    builder.append("  ")
        .append("public ").append(className)
        .append("(Router router) { this.router = router; }\n\n");
  }

  private void emitRoutes(StringBuilder builder) {
    builder.append("  ").append("public static PathPattern[] PATH_PATTERNS = {\n");

    boolean firstPathPattern = true;
    for (PathPatternBuilder pathPatternBuilder : pathPatternBuilders) {
      if (!firstPathPattern) {
        builder.append(",\n");
      }
      firstPathPattern = false;
      builder.append("    ").append(PathPattern.class.getSimpleName()).append(".create(");

      PathPattern pathPattern = pathPatternBuilder.typeMap(ownBinding.typeMap()).build();
      boolean firstPathPart = true;
      for (PathPart pathPart : pathPattern.parts()) {
        if (!firstPathPart) {
          builder.append(", ");
        }
        firstPathPart = false;
        if (pathPart instanceof StaticPart) {
          builder.append(StaticPart.class.getSimpleName());
          builder.append(".create(\"");
          // FIXME String code escape
          builder.append(((StaticPart) pathPart).value());
          builder.append("\"");
        } else if (pathPart instanceof DynamicPart) {
          DynamicPart dynamicPart = (DynamicPart) pathPart;
          builder.append(DynamicPart.class.getSimpleName());
          builder.append(".create(\"");
          // FIXME String code escape
          builder.append(dynamicPart.name());
          builder.append("\", \"");

            /* No need to escape as all of them are listed in BundleType */
          builder.append(dynamicPart.bundleType());
          builder.append("\", ");

          String quote = dynamicPart.constraint() != null ? "\"" : "";
          builder.append(quote).append(dynamicPart.constraint()).append(quote);
        } else {
          // For the future
          throw new RuntimeException("Unhandled PathPart: " + pathPart.getClass());
        }
        builder.append(")");
      }
      builder.append(")");
    }
    builder.append("\n  };\n\n");

    builder.append("  ").append("@Override public PathPattern[] pathPatterns() {\n");
    builder.append("    ").append("return PATH_PATTERNS;\n");
    builder.append("  ").append("}");
  }

  private void emitSaver(StringBuilder builder) {
    builder.append("  ")
        .append("@Override public void save(final T source, Bundle dest, boolean flat) {\n");

    if (parentInjector != null) {
      builder.append("    super.save(source, dest, flat);");
    }
    builder.append("    ")
        .append("Bundle toWrite = null;\n")
        .append("    ")
        .append("toWrite = flat ? dest : new Bundle();\n");

    for (FieldBinding binding: ownBinding.bindings()) {
      if (binding instanceof BundleableBinding) {
        emitSaveBinding(builder, (BundleableBinding) binding);
      } else {
        emitRedirectSaveBinding(builder, binding);
      }
    }

    builder.append("\n    ")
        .append("if (!flat) dest.putBundle(\"")
        .append(ownBinding.getName())
        .append("\", toWrite);\n");
    builder.append("  ")
        .append("}");
  }

  private void emitInject(StringBuilder builder) {
    builder.append("  ")
        .append("@Override public void inject(final T target, Bundle source) {\n");
    if (parentInjector != null) {
      builder.append("    super.inject(target, source);\n\n");
    }

    builder.append("    ")
        .append("Bundle subBundle = source.getBundle(\"")
        .append(ownBinding.getName())
        .append("\");\n");
    builder.append("    ")
        .append("if (subBundle != null) {\n");

    builder.append("      ")
        .append("inject(target, subBundle);\n");
    builder.append("    ")
        .append("}\n");

    for (FieldBinding binding: ownBinding.bindings()) {
      if (binding instanceof BundleableBinding) {
        emitInjectBinding(builder, (BundleableBinding) binding);
      } else if (binding instanceof IndirectBinding) {
        emitRedirectBinding(builder, (IndirectBinding) binding);
      } else {
        throw new IllegalStateException("Unknown FieldBinding type: " + binding.getClass());
      }
    }

    builder.append("  }");
  }

  private void emitRedirectBinding(StringBuilder builder, IndirectBinding binding) {
    String fieldPath = "target." + binding.getName();

    builder.append("    ")
        .append("if (")
        .append(fieldPath)
        .append(" == null) ")
        .append(fieldPath)
        .append(" = new ")
        .append(binding.getClassName())
        .append("();\n");

    builder.append("    ")
        .append("router.inject(target.")
        .append(binding.getName())
        .append(", source);\n");
  }

  private void emitInjectBinding(StringBuilder builder, BundleableBinding binding) {
    if (binding.isMethod() && !binding.getName().startsWith("set")) {
      return;
    }
    builder.append("    ")
        .append("if (source.containsKey(\"")
        .append(binding.getBundleKey())
        .append("\")) {\n");

    builder.append("      ")
        .append("target.")
        .append(binding.getName())
        .append(binding.isMethod() ? "(" : " = ")

        .append("source.get")
        .append(binding.getBundleMethod())
        .append("(\"")
        .append(binding.getBundleKey())
        .append("\")")

        .append(binding.isMethod() ? ")" : "")
        .append(";\n");
    builder.append("    ")
        .append("}\n");
  }

  private void emitSaveBinding(StringBuilder builder, BundleableBinding binding) {
    if (binding.isMethod() && !binding.getName().startsWith("get")) {
      return;
    }
    builder.append("    ")
        .append("toWrite.put")
        .append(binding.getBundleMethod())
        .append("(\"")
        .append(binding.getBundleKey())
        .append("\", source.")
        .append(binding.getName())
        .append(binding.isMethod() ? "()" : "")
        .append(")")
        .append(";\n");
  }

  private void emitRedirectSaveBinding(StringBuilder builder, FieldBinding binding) {
    builder.append("    ")
        .append("router.saveSingle(toWrite, source.")
        .append(binding.getName())
        .append(", flat)")
        .append(";\n");
  }

  public void addFieldBinding(FieldBinding binding) {
    ownBinding.addFieldBinding(binding);
  }

  public void addPathPatternBuilder(PathPatternBuilder pathPattern) {
    pathPatternBuilders.add(pathPattern);
  }

  public void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }

  public ClassBinding getOwnBinding() {
    return ownBinding;
  }
}
