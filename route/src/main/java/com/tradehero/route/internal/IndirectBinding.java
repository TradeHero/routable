package com.tradehero.route.internal;

import java.util.Map;

class IndirectBinding extends FieldBinding {
  private final String className;
  private final ClassBinding classBinding;

  /**
   * Binding for custom type, also see {@link com.tradehero.route.internal.BundleableBinding}
   * Example: {@code @RouteProperty com.example.SomeType val } has
   *  - name: val
   *  - className: com.example.SomeType
   */
  public IndirectBinding(String name, String className, ClassBinding classBinding) {
    super(name);
    this.className = className;
    this.classBinding = classBinding;
  }

  public String getClassName() {
    return className;
  }

  @Override public Map<String, BundleType> typeMap() {
    return classBinding.typeMap();
  }
}
