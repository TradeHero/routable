package com.tradehero.route;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/**
 * A dynamic part, which gets extracted into a parameter.
 */
@AutoValue
public abstract class DynamicPart implements PathPart {

  /**
   * THIS METHOD SHOULD ONLY BE ACCESSED VIA GENERATED CODE. For other purpose,
   * use internally its builder {@link com.tradehero.route.internal.PathPatternBuilder},
   * this way have advantage of type-safety provided by BundleType enum
   *
   * @param name The name of the parameter that this part of the path gets extracted into.
   * @param constraint The regular expression used to match this part.
   * @param bundleType The bundle-able type of variable associated with the name
   */
  public static DynamicPart create(String name, String bundleType, String constraint) {
    return new AutoValue_DynamicPart(name, bundleType, constraint);
  }

  public abstract String name();
  public abstract String bundleType();
  @Nullable public abstract String constraint();
}
