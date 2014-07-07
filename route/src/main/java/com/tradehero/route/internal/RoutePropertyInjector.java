package com.tradehero.route.internal;

import com.tradehero.route.Router;
import java.util.LinkedHashSet;
import java.util.Set;

final class RoutePropertyInjector {
  private final String classPackage;
  private final String targetClass;
  private String bundleKey;
  private final String className;
  private final Set<RoutePropertyBinding> fieldBinding = new LinkedHashSet<RoutePropertyBinding>();
  private String parentInjector;

  RoutePropertyInjector(String classPackage, String className, String targetClass, String bundleKey) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.bundleKey = bundleKey;
  }

  String getFqcn() {
    return classPackage + "." + className;
  }

  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code by Route. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("import android.os.Bundle;\n");
    builder.append("import ").append(Router.class.getName()).append(".Parser;\n\n");
    builder.append("public class ").append(className).append(" {\n");

    emitInject(builder);
    builder.append("\n\n");

    emitSaver(builder);
    builder.append("\n\n");

    emitReset(builder);

    builder.append("}\n");

    return builder.toString();
  }

  private void emitSaver(StringBuilder builder) {
    builder.append("  ")
        .append("public static void save(final ")
        .append(targetClass)
        .append(" source, Bundle dest, boolean flat) {\n");

    builder.append("    ")
        .append("Bundle toWrite = null;\n");

    for (RoutePropertyBinding binding: fieldBinding) {
      emitSaveBinding(builder, binding);
    }

    builder.append("\n    ")
        .append("if (!flat) dest.putBundle(\"")
        .append(bundleKey)
        .append("\", toWrite);\n");
    builder.append("  ")
        .append("}\n");
  }

  private void emitReset(StringBuilder builder) {
    // TODO what should be reseted after activity/fragment destroyed?
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

    for (RoutePropertyBinding binding: fieldBinding) {
      emitInjectBinding(builder, binding);
    }

    builder.append("  }");
  }

  private void emitInjectBinding(StringBuilder builder, RoutePropertyBinding binding) {
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
        .append("Parser.parse(source.get(\"")
        .append(binding.getBundleKey())
        .append("\"), ")
        .append(binding.getType())
        .append(".class")
        .append(")")
        .append(binding.isMethod() ? ")" : "")
        .append(";\n");
    builder.append("    ")
        .append("}\n");
  }

  private void emitSaveBinding(StringBuilder builder, RoutePropertyBinding binding) {
    if (binding.isMethod() && !binding.getName().startsWith("get")) {
      return;
    }

    builder.append("    ")
        .append("toWrite = flat ? dest : new Bundle();\n");
    builder.append("    ")
        .append("Parser.put(toWrite, \"")
        .append(binding.getBundleKey())
        .append("\", source.")
        .append(binding.getName())
        .append(binding.isMethod() ? "()" : "")
        .append(", ")
        .append(binding.getType())
        .append(".class")
        .append(")")
        .append(";\n");
  }

  public void addBinding(RoutePropertyBinding binding) {
    fieldBinding.add(binding);
  }

  public void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }
}
