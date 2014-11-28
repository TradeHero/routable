package com.tradehero.route.internal;

class FieldBinding {
  private String name;
  private String bundleMethod;

  public FieldBinding(String name, String bundleMethod) {
    this.name = name;
    this.bundleMethod = bundleMethod;
  }

  public String getBundleMethod() {
    return bundleMethod;
  }

  public String getName() {
    return name;
  }
}
