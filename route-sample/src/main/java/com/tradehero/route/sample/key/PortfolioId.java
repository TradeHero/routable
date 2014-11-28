package com.tradehero.route.sample.key;

import com.tradehero.route.RouteProperty;

public class PortfolioId {
  @RouteProperty int portfolioId;

  public PortfolioId() {
  }

  public PortfolioId(int portfolioId) {
    this.portfolioId = portfolioId;
  }

  public int getId() {
    return portfolioId;
  }
}
