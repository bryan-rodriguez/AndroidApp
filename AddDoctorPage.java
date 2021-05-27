package com.example.phms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class AddDoctorPage extends AppCompatActivity
{
    TextView tvDate;
    String userID, doctorsName, doctorType, annualDateStr;
    Button submitButton, cancelButton, addCheckups;
    EditText doctorN, doctorT;
    List<TextView> allCheckups;
    LinearLayout ll;
    int numOfLines;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor_page);

        allCheckups = new ArrayList<>();

        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
        addCheckups = findViewById(R.id.addCheckUp);
        tvDate = findViewById(R.id.tvDate);
        doctorN = findViewById(R.id.doctorName);
        doctorT = findViewById(R.id.doctorSpec);
        ll = findViewById(R.id.layout);
        numOfLines = 4;


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userID).collection("Doctors").document();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddDoctorPage.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        String date = year + "/";
                        month = month + 1;
                        if(month < 10)
                        {
                            //date.concat("0" + String.valueOf(month) + "/");
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
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        addCheckups.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                TextView dynamicTVDate = new TextView(AddDoctorPage.this);
                dynamicTVDate.setText("Click here to select a date");
                dynamicTVDate.setId(ViewCompat.generateViewId());
                dynamicTVDate.setTextSize(18);

                dynamicTVDate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                AddDoctorPage.this, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day)
                            {
                                String date = year + "/";
                                month = month + 1;
                                if(month < 10)
                                {
                                    //date.concat("0" + String.valueOf(month) + "/");
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
                                dynamicTVDate.setText(date);
                            }
                        }, year, month, day);
                        datePickerDialog.show();
                    }


                });
                dynamicTVDate.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        dynamicTVDate.setText("Click here to select a date");
                        return false;
                    }
                });
                allCheckups.add(dynamicTVDate);
                ll.addView(dynamicTVDate, numOfLines);
                numOfLines++;
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), DoctorPage.class));
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                List<Integer> allDates = new ArrayList<Integer>();
                doctorsName = doctorN.getText().toString();
                doctorType = doctorT.getText().toString().trim();

                if(doctorsName.isEmpty())
                {
                    doctorN.setError("Doctor must have a name");
                    return;
                }
                if(doctorType.isEmpty())
                {
                    doctorT.setError("Doctor's type must be specified");
                    return;
                }

                for(TextView tv : allCheckups)
                {
                    if(!tv.getText().toString().matches("Click here to select a date"))
                    {
                        String dateStr = tv.getText().toString().trim();
                        dateStr = dateStr.replaceAll("/","");
                        Integer date = Integer.parseInt(dateStr);
                        allDates.add(date);
                    }
                }
                if(allDates.isEmpty())
                {
                    Toast.makeText(AddDoctorPage.this, "You must select a checkup date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tvDate.getText().toString().matches("Click here to select an annual visit"))
                {
                    Toast.makeText(AddDoctorPage.this, "You must a select an annual visit", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> doctorData = new HashMap<>();
                annualDateStr = tvDate.getText().toString();
                annualDateStr = annualDateStr.replaceAll("/","");
                Integer annualDate = Integer.parseInt(annualDateStr);
                doctorData.put("Name", doctorsName);
                doctorData.put("Type", doctorType);
                doctorData.put("Visits", allDates);
                doctorData.put("Annual Visit", annualDate);

                documentReference.set(doctorData);
                Toast.makeText(AddDoctorPage.this, "Doctor Successfully Created", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), DoctorPage.class));
            }
        });


    }
}