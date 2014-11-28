package com.tradehero.route.sample;

import android.app.Activity;
import com.tradehero.route.RouteProperty;
import com.tradehero.route.sample.key.PortfolioId;
import com.tradehero.route.sample.key.UserBaseKey;

public class DummySuperActivity extends Activity {
  @RouteProperty PortfolioId portfolioId;
  @RouteProperty UserBaseKey userBaseKey;

  @RouteProperty String username;
}
