package com.example.phms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpPage extends AppCompatActivity
{
    EditText firstname, lastname, age, email, pass, pass2;
    Button Submit;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView loginHere;
    LinearLayout ll;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        firstname = findViewById(R.id.name1);
        lastname = findViewById(R.id.name2);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        pass2 = findViewById(R.id.repassword);
        Submit = findViewById(R.id.Submit_Button);
        loginHere = findViewById(R.id.already_Account);
        ll = findViewById(R.id.layout);

        Spinner spinner = (Spinner) findViewById(R.id.Gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array ,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Femail = email.getText().toString().trim();
                String Fpass = pass.getText().toString().trim();
                String gender = spinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(Femail))
                {
                    email.setError("Email is Required");
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

               // progressBar.setVisibility(View.VISIBLE);

                //register in firebase
                fAuth.createUserWithEmailAndPassword(Femail,Fpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Users").document(userID);
                            Map<String, Object> Users = new HashMap<>();
                            Users.put("First Name", firstname.getText().toString().trim());
                            Users.put("Last Name", lastname.getText().toString().trim());
                            Users.put("Age", age.getText().toString().trim());
                            Users.put("Gender", gender);
                            Users.put("Height", 0.0);
                            Users.put("Next of Kin", "No Name");
                            Users.put("Next of Kin #", "No #");

                            documentReference.set(Users);
                            Toast.makeText(SignUpPage.this, "User Created", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        }
                        else
                        {
                            Toast.makeText(SignUpPage.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

        loginHere.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });


    }
}