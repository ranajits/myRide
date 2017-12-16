package rnjt.com.myride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserDashboardActivity extends AppCompatActivity {

    TextView txtuname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        txtuname= (TextView) findViewById(R.id.txtuname);


        SharedPreferences sharedPreferences = getSharedPreferences("my_ride", MODE_PRIVATE);
        sharedPreferences.edit().putString("user_type", "type_user").commit();

        if(getIntent().hasExtra("dr_name")) {
            txtuname.setText(getIntent().getStringExtra("dr_name"));
            sharedPreferences.edit().putString("dr_name", getIntent().getStringExtra("dr_name")).commit();
        }else {
            txtuname.setText(sharedPreferences.getString("dr_name", "Raj Malhotra"));
        }

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


        findViewById(R.id.imgLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("my_ride", MODE_PRIVATE).edit().clear().commit();
                startActivity(new Intent(UserDashboardActivity.this, SplashScreen.class));
                finish();

            }
        });


    }
}
