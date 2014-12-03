package com.tradehero.route.internal;

import com.tradehero.route.DynamicPart;
import com.tradehero.route.PathPart;
import com.tradehero.route.PathPattern;
import com.tradehero.route.StaticPart;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class RouteInjector {
  private final String classPackage;
  private final String targetClass;
  private final String bundleKey;
  private final String className;
  private final Set<FieldBinding> fieldBinding = new LinkedHashSet<FieldBinding>();
  private final Set<PathPatternBuilder> pathPatternBuilders = new LinkedHashSet<PathPatternBuilder>();
  private final Map<String, BundleType> typeMapRepo = new HashMap<String, BundleType>();
  private String parentInjector;

  RouteInjector(String classPackage, String className, String targetClass, String bundleKey) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.bundleKey = bundleKey;
  }

  String getFqcn() {
    return Utils.isNullOrEmpty(classPackage) ? className : classPackage + "." + className;
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
    }
    builder.append("\n");

    // class content
    builder.append("public final class ").append(className).append(" {\n");
    if (pathPatternBuilders.size() > 0) {
      emitRoutes(builder);
      builder.append("\n\n");
    }

    if (fieldBinding.size() > 0) {
      emitInject(builder);
      builder.append("\n\n");

      emitSaver(builder);
      builder.append("\n");
    }

    builder.append("}\n");

    return builder.toString();
  }

  private void emitRoutes(StringBuilder builder) {
    builder.append("  ").append("public static PathPattern[] PATH_PATTERNS = {\n");

    for (FieldBinding binding: fieldBinding) {
      if (binding instanceof BundleableBinding) {
        BundleableBinding routeBinding = (BundleableBinding) binding;
        typeMapRepo.put(routeBinding.getBundleKey(), routeBinding.getBundleMethod());
      }
    }

    boolean firstPathPattern = true;
    for (PathPatternBuilder pathPatternBuilder : pathPatternBuilders) {
      if (!firstPathPattern) {
        builder.append(",\n");
      }
      firstPathPattern = false;
      builder.append("    ").append(PathPattern.class.getSimpleName()).append(".create(");

      PathPattern pathPattern = pathPatternBuilder.typeMap(typeMapRepo).build();
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
    builder.append("};");
  }

  private void emitSaver(StringBuilder builder) {
    builder.append("  ")
        .append("public static void save(final ")
        .append(targetClass)
        .append(" source, Bundle dest, boolean flat) {\n");

    builder.append("    ")
        .append("Bundle toWrite = null;\n")
        .append("    ")
        .append("toWrite = flat ? dest : new Bundle();\n");

    for (FieldBinding binding: fieldBinding) {
      if (binding instanceof BundleableBinding) {
        emitSaveBinding(builder, (BundleableBinding) binding);
      } else {
        emitRedirectSaveBinding(builder, binding);
      }
    }

    builder.append("\n    ")
        .append("if (!flat) dest.putBundle(\"")
        .append(bundleKey)
        .append("\", toWrite);\n");
    builder.append("  ")
        .append("}");
  }

  private void emitInject(StringBuilder builder) {
    builder.append("  ")
        .append("public static void inject(final ")
        .append(targetClass)
        .append(" target, Bundle source) {\n");
    // Emit a call to the superclass injector, if any.
    if (parentInjector != null) {
      builder.append("    ")
          .append(parentInjector)
          .append(".inject(target, source);\n\n");
    }

    builder.append("    ")
        .append("Bundle subBundle = source.getBundle(\"")
        .append(bundleKey)
        .append("\");\n");
    builder.append("    ")
        .append("if (subBundle != null) {\n");

    builder.append("      ")
        .append("inject(target, subBundle);\n");
    builder.append("    ")
        .append("}\n");

    for (FieldBinding binding: fieldBinding) {
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
        .append(binding.creatorName)
        .append("();\n");

    builder.append("    ")
        .append("Router.getInstance().inject(target.")
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
        .append("Router.getInstance().saveSingle(toWrite, source.")
        .append(binding.getName())
        .append(", flat)")
        .append(";\n");
  }

  public void addFieldBinding(FieldBinding binding) {
    fieldBinding.add(binding);
  }
  public void addPathPatternBuilder(PathPatternBuilder pathPattern) {
    pathPatternBuilders.add(pathPattern);
  }

  public void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }
}
