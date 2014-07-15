package com.tradehero.route.internal;

public class RoutePropertyBinding extends FieldBinding {
  private final boolean isMethod;
  private final String bundleKey;

  public RoutePropertyBinding(String name, String bundleMethod, String bundleKey, boolean isMethod) {
    super(name, bundleMethod);
    this.bundleKey = bundleKey;
    this.isMethod = isMethod;
  }

  public String getBundleKey() {
    return bundleKey != null && bundleKey.length() > 0 ? bundleKey : getName();
  }

  public boolean isMethod() {
    return isMethod;
  }
}
