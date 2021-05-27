package com.example.phms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPastWeightPage extends AppCompatActivity
{
    EditText weightET;
    Button cancel, submit;
    String userID;
    TextView tvDate;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_past_weight_page);

        cancel = findViewById(R.id.cancelWeightButton);
        submit = findViewById(R.id.submitWeightButton);
        tvDate = findViewById(R.id.tvWeightDate);
        weightET = findViewById(R.id.weightID);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("Users").document(userID).collection("Weights").document();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddPastWeightPage.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        String date = year + "/";
                        month = month + 1;
                        if(month < 10)
                        {
                            date = date + "0" + month + "/";
                        }
                        else
                        {
                            date = date + month + "/";
                        }
                        if(day < 10)
                        {
                            date = date + "0" + day;
                        }
                        else
                        {
                            date = date + day;
                        }
                        tvDate.setText(date);
                    }
                }, year, month, day - 1);
                datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String weightStr = weightET.getText().toString().trim();
                String dateStr = tvDate.getText().toString();
                if(weightStr.isEmpty())
                {
                    weightET.setError("Must add a weight");
                    return;
                }
                if(dateStr.matches("Click me to select a time"))
                {
                    tvDate.setError("Must select a time");
                    //Toast.makeText(AddPastWeightPage.this, "Must select a time" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer date = Integer.parseInt(dateStr.replaceAll("/",""));
                Map<String, Object> weightData = new HashMap<>();
                weightData.put("Weight", Double.valueOf(weightStr));
                weightData.put("Date", date);

                documentReference.set(weightData);
                Toast.makeText(AddPastWeightPage.this, "Weight Added Successfully" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),PersonalInfoPage.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),PersonalInfoPage.class));
            }
        });
    }
}