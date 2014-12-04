package com.tradehero.route.internal;

import java.util.Collections;
import java.util.Map;

final class BundleableBinding extends FieldBinding {
  private final boolean isMethod;
  private final BundleType bundleMethod;
  private final String bundleKey;

  /**
   * Binding for types which is supported by Bundle (Int, String, Short...)
   * For list of supporting type, see {@link com.tradehero.route.internal.BundleType},
   * for the other types, use {@link com.tradehero.route.internal.IndirectBinding}
   *
   * Example: {@code @RouteProperty("foo") Integer bar } has
   *  - name: bar
   *  - bundleMethod: Int
   *  - bundleKey: foo
   *  - isMethod: false
   * using that binding we can generate {@code bundle.putInt("foo", bar) }
   *
   * @param name name of the element (field or method) which annotated by @RouteProperty
   * @param bundleMethod method from Bundle for the element
   * @param bundleKey associated key of the element
   * @param isMethod true if element is a method, false otherwise
   */
  public BundleableBinding(String name, BundleType bundleMethod, String bundleKey, boolean isMethod) {
    super(name);
    this.bundleMethod = bundleMethod;
    this.bundleKey = bundleKey;
    this.isMethod = isMethod;
  }

  public BundleType getBundleMethod() {
    return bundleMethod;
  }

  public String getBundleKey() {
    return bundleKey != null && bundleKey.length() > 0 ? bundleKey : getName();
  }

  public boolean isMethod() {
    return isMethod;
  }

  @Override public Map<String, BundleType> typeMap() {
    return Collections.singletonMap(getBundleKey(), bundleMethod);
  }
}
