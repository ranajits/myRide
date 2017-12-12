package rnjt.com.myride;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DriverActivity extends AppCompatActivity {


    TextView txtdName, txtDLocation, txtdMobile, txtvNumber, txtvMOdel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        txtdName= (TextView) findViewById(R.id.txtdName);
        txtDLocation= (TextView) findViewById(R.id.txtdLocation);
        txtdMobile= (TextView) findViewById(R.id.txtdMobile);
        txtvNumber= (TextView) findViewById(R.id.txtvNumber);
        txtvMOdel= (TextView) findViewById(R.id.txtvModel);

        SharedPreferences sharedPreferences = getSharedPreferences("my_ride", MODE_PRIVATE);
        if(getIntent().hasExtra("dr_name")) {
            txtdName.setText(getIntent().getStringExtra("dr_name"));
            txtDLocation.setText("Mumbai, Maharashtra");
            txtdMobile.setText(getIntent().getStringExtra("dr_email"));
            txtvNumber.setText(getIntent().getStringExtra("dr_vh_number"));
            txtvMOdel.setText(getIntent().getStringExtra("dr_vh_model"));



            sharedPreferences.edit().putString("user_type", "type_driver").commit();
            sharedPreferences.edit().putString("dr_name",  ""+ txtdName.getText().toString()).commit();
            sharedPreferences.edit().putString("dr_email",  ""+ txtdMobile.getText().toString()).commit();
            sharedPreferences.edit().putString("dr_vh_number", ""+ txtvNumber.getText().toString()).commit();
            sharedPreferences.edit().putString("dr_vh_model", ""+ txtvMOdel.getText().toString()).commit();


        }else {
            txtdName.setText(sharedPreferences.getString("dr_name", "Ritu Parashar"));
            txtDLocation.setText("Mumbai, Maharashtra");
            txtdMobile.setText(sharedPreferences.getString("dr_email", "ritu@gmail.com"));
            txtvNumber.setText(sharedPreferences.getString("dr_vh_number", "8879917028"));
            txtvMOdel.setText(sharedPreferences.getString("dr_vh_model", "Maruti Suzuki Ritz"));

        }






    }
}
