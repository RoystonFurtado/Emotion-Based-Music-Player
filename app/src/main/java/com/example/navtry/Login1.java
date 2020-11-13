package com.example.navtry;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login1 extends BaseActivity {
    EditText a1,a2;
    Button b2;
    Dbhelper db = new Dbhelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_login1, null, false);
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity3);

        a1 = (EditText)findViewById(R.id.email1);
        a2 =(EditText)findViewById(R.id.pass1);
        b2 =(Button)findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st1 = a1.getText().toString();
                String st2 = a2.getText().toString();
                if (!(validateloginmail())) {
                    a1.setError("Fields cant be empty");
                } else if(!(validateloginpass())){
                    a2.setError("Password cant be empty");
                }
                if (validateloginpass() && validateloginmail()) {
                    boolean chkemailpass = db.emailpassword(st1, st2);
                    if (chkemailpass == true) {
                        Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    public  boolean validateloginmail(){
        String f1 = a1.getText().toString();
        String f2 = a2.getText().toString();
        if (f1.isEmpty()){
            return false;
        }
        return true;
    }
    public boolean validateloginpass(){
        String f2 = a2.getText().toString();
        if (f2.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        startAnimatedActivity(new Intent(getApplicationContext(), FirstActivity.class));
        finish();

    }

}

