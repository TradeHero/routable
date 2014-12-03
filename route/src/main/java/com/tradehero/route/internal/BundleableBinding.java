package com.tradehero.route.internal;

final class BundleableBinding extends FieldBinding {
  private final boolean isMethod;
  private final BundleType bundleMethod;
  private final String bundleKey;

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
}
