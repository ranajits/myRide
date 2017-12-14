package rnjt.com.myride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        SharedPreferences sharedPreferences = getSharedPreferences("my_ride", MODE_PRIVATE);
        sharedPreferences.edit().putString("user_type", "type_user").commit();
        findViewById(R.id.imgAccident).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDashboardActivity.this, ShowRouteActivity.class));

            }
        });


        findViewById(R.id.imgHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDashboardActivity.this, BookingHistoryActivity.class));

            }
        });

    }
}
