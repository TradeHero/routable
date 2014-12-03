package com.tradehero.route.internal;

class RedirectBinding extends FieldBinding {
  final String creatorName;

  public RedirectBinding(String name, String creatorName) {
    super(name);
    this.creatorName = creatorName;
  }
}
