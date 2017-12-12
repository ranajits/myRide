package rnjt.com.myride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        findViewById(R.id.img_driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("user_type", "driver"));
                finish();
            }
        });



        findViewById(R.id.imgUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("user_type", "user"));
                finish();
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("my_ride", MODE_PRIVATE);
        if(sharedPreferences.getString("user_type", "user_type").equals("type_driver")){
            startActivity(new Intent(SplashActivity.this, DriverActivity.class));
            finish();
        }else if(sharedPreferences.getString("user_type", "user_type").equals("type_user")){
            startActivity(new Intent(SplashActivity.this, UserDashboardActivity.class));
            finish();
        }


    }
}
