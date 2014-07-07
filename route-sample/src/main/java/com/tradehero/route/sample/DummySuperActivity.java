package com.tradehero.route.sample;

import android.app.Activity;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.sample.key.PortfolioId;
import com.tradehero.route.sample.key.UserBaseKey;

public class DummySuperActivity extends Activity {
  @InjectRoute PortfolioId portfolioId;
  @InjectRoute UserBaseKey userBaseKey;
}
