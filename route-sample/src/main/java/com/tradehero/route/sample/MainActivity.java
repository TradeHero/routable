package com.tradehero.route.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.tradehero.route.Router;
import com.tradehero.route.sample.key.PortfolioId;
import com.tradehero.route.sample.key.UserBaseKey;

public class MainActivity extends Activity {
  private Router router;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    router = App.get(this).getRouter();

    TextView openByRoute = (TextView) findViewById(R.id.open_by_route);
    openByRoute.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        router.open("/users/16");
      }
    });

    TextView openManually = (TextView) findViewById(R.id.open_manually);
    openManually.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Bundle bundle = new Bundle();
        UserBaseKey userBaseKey = new UserBaseKey(10);
        PortfolioId portfolioId = new PortfolioId(20);
        router.save(bundle, userBaseKey, portfolioId, false);
        bundle.putString("username", "Tho Nguyen");

        Intent intent = new Intent(MainActivity.this, UserActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
      }
    });
  }
}
