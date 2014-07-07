package com.tradehero.route.sample;

import android.app.Activity;
import android.os.Bundle;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.route.Router;
import com.tradehero.route.sample.key.SecurityId;

@Routable(
    "security/:exchangeName/:securitySymbol"
)
public class SecurityActivity extends Activity {
  @InjectRoute SecurityId securityId;
  private Router router;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    router = ((App) getApplication()).getRouter();

    router.inject(this, getIntent().getExtras());
  }

  @Override protected void onDestroy() {
    router.reset(this);
    super.onDestroy();
  }
}
