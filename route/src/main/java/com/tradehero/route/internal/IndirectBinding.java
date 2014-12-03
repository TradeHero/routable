package com.tradehero.route.internal;

class IndirectBinding extends FieldBinding {
  final String creatorName;

  public IndirectBinding(String name, String creatorName) {
    super(name);
    this.creatorName = creatorName;
  }
}
