package com.tradehero.route.sample.key;

import com.tradehero.route.RouteProperty;

public class SecurityId {
  @RouteProperty("exchangeName") String exchangeName;
  @RouteProperty("securitySymbol") String securitySymbol;
}
