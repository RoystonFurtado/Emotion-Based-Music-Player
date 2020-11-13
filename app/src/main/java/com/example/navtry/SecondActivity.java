package com.example.navtry;


import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;



import java.util.Calendar;
import java.util.regex.Pattern;


public class SecondActivity extends BaseActivity {

    Dbhelper db;
    EditText e1,e2,e3;
    Button b1,b3,b5;
    Calendar c;
    DatePickerDialog dpd;
    TextView v1;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
    private long backpressedtime;
    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_second, null, false);
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity2);

        db = new Dbhelper(this);
        e1 = (EditText) findViewById(R.id.email);
        e2 = (EditText) findViewById(R.id.pass);
        e3 = (EditText) findViewById(R.id.cpass);
        b1 = (Button) findViewById(R.id.button);
        v1 = (TextView)findViewById(R.id.textView);
        b5 = (Button)findViewById(R.id.button5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                dpd = new DatePickerDialog(SecondActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Y, int M, int D) {
                        v1.setText(D+"/"+(M+1)+"/"+Y);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = e1.getText().toString();
                String s2 = e2.getText().toString();
                String s3 = e3.getText().toString();
                if (!(validatePassword() || validatmail())){
                    e1.requestFocus();
                    Toast.makeText(getApplicationContext(),"Enter valid fields",Toast.LENGTH_SHORT);
                }

                if (s2.equals(s3)) {
                    boolean checkmail = db.chkemail(s1);
                    if (validatmail() && validatePassword()) {
                        if (checkmail){
                            boolean insert = db.insert(s1, s2);
                            if (insert == true) {
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "email already exists  ", Toast.LENGTH_SHORT).show();
                            e1.requestFocus();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Enter valid Fields",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "passwords do not match", Toast.LENGTH_SHORT).show();
                    e3.requestFocus();
                }

            }



        });


    }



    public boolean validatmail() {

        String s1 = e1.getText().toString();
        if (s1.isEmpty()) {
            e1.setError("Fields cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s1).matches()) {
            e1.setError("Enter a valid email address");
            return false;
        } else {
            e1.setError(null);
            return true;
        }
    }


    public boolean validatePassword() {
        String passwordInput = e2.getText().toString();

        if (passwordInput.isEmpty()) {
            e2.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            e2.setError("Password too weak");
            return false;
        } else {
            e2.setError(null);
            return true;
        }

    }


    @Override
    public void onBackPressed() {

        startAnimatedActivity(new Intent(getApplicationContext(), FirstActivity.class));
        finish();

    }


}
