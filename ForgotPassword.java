package com.example.phms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity
{
    EditText emailStr;
    TextView signUp;
    Button Submit;
    FirebaseAuth fAuth;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Submit = findViewById(R.id.submit_Button);
        fAuth = FirebaseAuth.getInstance();
        signUp = findViewById(R.id.already_Account);
        emailStr = findViewById(R.id.forgotEmail);

        Submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String forgotEmail = emailStr.getText().toString().trim();

                if(TextUtils.isEmpty(forgotEmail))
                {
                    emailStr.setError("Email is Required");
                    return;
                }


                fAuth.sendPasswordResetEmail(forgotEmail).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ForgotPassword.this, "Email Successfully Sent", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        }
                        else {
                            Toast.makeText(ForgotPassword.this, "Email Does Not Exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });


    }
}