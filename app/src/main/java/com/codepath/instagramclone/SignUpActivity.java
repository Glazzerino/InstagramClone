package com.codepath.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etEmail;
    EditText etPassword;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsernameSignup);
        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPasswordSignup);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                //no field must be empty
                if ( !username.isEmpty() && !password.isEmpty() && !email.isEmpty())
                    createAccount(username, password, email);
                else
                    Toast.makeText(SignUpActivity.this,"You must fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount(String username, String password, String email) {
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        //Signup callback

        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //Notify new user creation and go to login
                if (e == null) {
                    Toast.makeText(SignUpActivity.this, "Welcome to an instagram clone!", Toast.LENGTH_SHORT).show();
                    //TODO: make signup auto-login
                    finish();
                } else {
                    Log.e("SignUpActivity", "Error creating new user on Parser: "+ e.toString()) ;
                }
            }
        });
    }
}