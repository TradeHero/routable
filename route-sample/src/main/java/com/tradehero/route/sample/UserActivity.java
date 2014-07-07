package com.tradehero.route.sample;

import android.os.Bundle;
import android.widget.TextView;
import com.tradehero.route.Routable;
import com.tradehero.route.Router;

@Routable({
    "users/:userId", "users/:userId/portfolio/:portfolioId"
})
public class UserActivity extends DummySuperActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Router router = ((App) getApplication()).getRouter();
    router.inject(this);

    setContentView(R.layout.user);

    TextView userInfo = (TextView) findViewById(R.id.user);
    userInfo.setText("User: " + userBaseKey.getUserId() + "\nPortfolio: " + portfolioId.getId());
  }
}
