package rnjt.com.myride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    String user_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_type= getIntent().getStringExtra("user_type");

        ImageView  img_usertype= (ImageView) findViewById(R.id.imguser);
        if(user_type.equals("driver")){
            img_usertype.setImageResource(R.drawable.driver_logo);
        }else {
            img_usertype.setImageResource(R.drawable.user_logo);
        }



        findViewById(R.id.imgFb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)
                        .putExtra("user_type", user_type)
                        .putExtra("login_type", "type_fb"));


            }
        });


        findViewById(R.id.txtMob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)
                        .putExtra("user_type", user_type)
                        .putExtra("login_type", "type_mobile"));


            }
        });



        findViewById(R.id.imgGmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)
                        .putExtra("user_type", user_type)
                        .putExtra("login_type", "type_fb"));


            }
        });
    }
}
