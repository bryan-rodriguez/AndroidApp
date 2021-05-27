package com.example.phms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginPage extends AppCompatActivity {
    ProgressBar progressBar;
    Button Login;
    EditText user, pass;
    FirebaseAuth fAuth;
    TextView signUpHere;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        user = findViewById(R.id.Username);
        pass = findViewById(R.id.Password);
        Login = findViewById(R.id.Login_Button);
        fAuth = FirebaseAuth.getInstance();
        signUpHere = findViewById(R.id.No_Account);
        forgotPassword = findViewById(R.id.forgotPass);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Femail = user.getText().toString().trim();
                String Fpass = pass.getText().toString().trim();

                if(TextUtils.isEmpty(Femail))
                {
                    user.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(Fpass))
                {
                    pass.setError("Password is Required");
                    return;
                }

                if(Fpass.length() < 6)
                {
                    pass.setError("Password Must be 6 char or longer");
                    return;
                }

                //authenticate user
                fAuth.signInWithEmailAndPassword(Femail,Fpass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                        }
                        else
                        {
                            Toast.makeText(LoginPage.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                fAuth.fetchSignInMethodsForEmail(Femail);
            }
        });

        signUpHere.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), SignUpPage.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });

    }



}