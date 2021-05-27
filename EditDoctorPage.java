package com.example.phms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDoctorPage extends AppCompatActivity
{

    TextView tvDate;
    String userID, doctorsName, doctorType, annualDateStr;
    Button submitButton, cancelButton, addCheckups;
    EditText doctorN, doctorT;
    List<TextView> allCheckups;
    LinearLayout ll;
    String documentID;
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
        documentID = getIntent().getStringExtra("Document ID");
        numOfLines = 4;


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("Users").document(userID).collection("Doctors").document(documentID);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
            {
                doctorN.setHint("Doctor's Name - " + value.getString("Name"));
                doctorT.setHint("Doctor's Specialty - " + value.getString("Type"));

                String tvdateStr = String.valueOf(value.get("Annual Visit"));
                String tvDateYearStr = tvdateStr.substring(0,4);
                String tvDateMonthStr = tvdateStr.substring(4,6);
                String tvDateDayStr = tvdateStr.substring(6,8);
                tvDate.setText(tvDateYearStr + "/" + tvDateMonthStr + "/" + tvDateDayStr);

                List<Long> allCheckUpss = (List<Long>) value.get("Visits");

                for(Long dateInt : allCheckUpss)
                {
                    String dateStr = String.valueOf(dateInt);
                    String yearStr = dateStr.substring(0,4);
                    String monthStr = dateStr.substring(4,6);
                    String dayStr = dateStr.substring(6,8);
                    Integer yearInt = Integer.parseInt(yearStr);

                    TextView newDynamicTVDate = new TextView(EditDoctorPage.this);
                    newDynamicTVDate.setText(yearStr + "/" + monthStr + "/" + dayStr);
                    newDynamicTVDate.setId(ViewCompat.generateViewId());
                    newDynamicTVDate.setTextSize(18);

                    if(monthStr.charAt(0) == '0')
                    {
                        monthStr = monthStr.replaceAll("0","");
                    }
                    if(dayStr.charAt(0) == '0')
                    {
                        dayStr = dayStr.replaceAll("0", "");
                    }
                    Integer dayInt = Integer.parseInt(dayStr);
                    Integer monthInt = Integer.parseInt(monthStr);

                    newDynamicTVDate.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            newDynamicTVDate.setText("Click here to select a date");
                            return true;
                        }
                    });

                    newDynamicTVDate.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    EditDoctorPage.this, new DatePickerDialog.OnDateSetListener()
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
                                    newDynamicTVDate.setText(date);
                                }
                            }, yearInt, monthInt - 1, dayInt);
                            datePickerDialog.show();
                        }

                    });
                    //THE BUG COULD BE HERE
                    allCheckups.add(newDynamicTVDate);
                    ll.addView(newDynamicTVDate, numOfLines);
                    numOfLines++;
                }
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditDoctorPage.this, new DatePickerDialog.OnDateSetListener()
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
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        tvDate.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                tvDate.setText("Click here to select an annual visit");
                return false;
            }
        });


        addCheckups.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextView dynamicTVDate = new TextView(EditDoctorPage.this);
                dynamicTVDate.setText("Click here to select a date");
                dynamicTVDate.setId(ViewCompat.generateViewId());
                dynamicTVDate.setTextSize(18);

                dynamicTVDate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                EditDoctorPage.this, new DatePickerDialog.OnDateSetListener()
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
                Map<String, Object> doctorData = new HashMap<>();
                if(!doctorsName.isEmpty())
                {
                    doctorData.put("Name", doctorsName);
                }
                if(!doctorType.isEmpty())
                {
                    doctorData.put("Type", doctorType);
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
                    Toast.makeText(EditDoctorPage.this, "You must select a checkup date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tvDate.getText().toString().matches("Click here to select an annual visit"))
                {
                    Toast.makeText(EditDoctorPage.this, "You must a select an annual visit", Toast.LENGTH_SHORT).show();
                    return;
                }

                annualDateStr = tvDate.getText().toString();
                annualDateStr = annualDateStr.replaceAll("/","");
                Integer annualDate = Integer.parseInt(annualDateStr);

                doctorData.put("Visits", allDates);
                doctorData.put("Annual Visit", annualDate);

                documentReference.update(doctorData);
                Toast.makeText(EditDoctorPage.this, "Doctor Successfully Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), DoctorPage.class));
            }
        });




    }
}