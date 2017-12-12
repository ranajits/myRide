package rnjt.com.myride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class RegistrationActivity extends AppCompatActivity {

    String user_type, login_type;
    ImageView img_usertype;
    EditText etMob, etEmail, etVehModel, etVehNumber, etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        user_type= getIntent().getStringExtra("user_type");
        login_type= getIntent().getStringExtra("login_type");

        img_usertype= (ImageView) findViewById(R.id.img_usertype);
        etMob= (EditText) findViewById(R.id.edtMob);
        etEmail= (EditText) findViewById(R.id.edtEmail);
        etVehModel= (EditText) findViewById(R.id.edtModel);
        etVehNumber= (EditText) findViewById(R.id.edtNumber);
        etName= (EditText) findViewById(R.id.edtName);

        if(user_type.equals("driver")){
            img_usertype.setImageResource(R.drawable.driver_logo);
        }else {
            img_usertype.setImageResource(R.drawable.user_logo);
            etVehModel.setVisibility(View.GONE);
            etVehNumber.setVisibility(View.GONE);

        }


        if(login_type.equals("type_mobile")){
        }else {

        }
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("driver")){
                    Intent intent= new Intent(RegistrationActivity.this, DriverActivity.class);

                    intent  .putExtra("dr_name", ""+etName.getText().toString());
                    intent  .putExtra("dr_email", ""+etEmail.getText().toString());
                    intent  .putExtra("dr_vh_model", ""+etVehModel.getText().toString());
                    intent  .putExtra("dr_vh_number", ""+etVehNumber.getText().toString());
                    intent  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                    //);
                    //finish();

                }else {

                    Intent intent= new Intent(RegistrationActivity.this, UserDashboardActivity.class);
                    intent  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }
        });



    }
}
