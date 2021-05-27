package com.example.phms;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class PersonalInfoPage extends AppCompatActivity {
    EditText firstname, lastname, age, weight, height, NoK, NoKNum;
    String gender, firstN, lastN, ageString, NokStr, userID, phoneNum, heightStr;
    double weightF, heightF;
    Button updateButton, cancelButton, addPastWeightButton, viewWeightsButton;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_page);

        firstname = findViewById(R.id.name1);
        lastname = findViewById(R.id.name2);
        age = findViewById(R.id.age);
        heightF = 0.0;
        weightF = 0.0;

        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        NoK = findViewById(R.id.nextOfKin);
        NoKNum = findViewById(R.id.nokNum);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancelButton);
        addPastWeightButton = findViewById(R.id.pastWeightButton);
        viewWeightsButton = findViewById(R.id.viewWeights);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("Users").document(userID);


        CollectionReference weightCollection = fStore.collection("Users").document(userID).collection("Weights");

        weightCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots.size() != 0)
                {
                    List<DocumentSnapshot> queryDocs = queryDocumentSnapshots.getDocuments();
                    List<Date> listDates = new ArrayList<>();
                    Date latestDate;
                    for(DocumentSnapshot A : queryDocs)
                    {
                        if(A.contains("Date CT"))
                        {
                            listDates.add(A.getDate("Date CT"));
                        }
                    }
                    for(DocumentSnapshot A : queryDocs)
                    {
                        if(A.contains("Date CT"))
                        {
                            latestDate = Collections.max(listDates);
                            if(A.getDate("Date CT").equals(latestDate))
                            {
                                weight.setHint("Weight (in lbs) - " + A.getDouble("Weight"));
                            }
                        }
                    }
                }
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.Gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String genderDoc = value.getString("Gender");
                String firstDoc = value.getString("First Name");
                String lastDoc = value.getString("Last Name");
                String ageStr = value.getString("Age");
                String nextOfKin = value.getString("Next of Kin");
                String nokPhoneNum = value.getString("Next of Kin #");
                Double heightD = value.getDouble("Height");

                firstname.setHint("First Name - " + firstDoc);
                lastname.setHint("Last Name - " + lastDoc);
                age.setHint("Age - " + ageStr);
                spinner.setSelection(adapter.getPosition(genderDoc));
                NoK.setHint("Next of Kin - " + nextOfKin);
                NoKNum.setHint("Phone number of Next of Kin - " + nokPhoneNum);

                if(heightD != 0.0)
                {
                    height.setHint("Height (in cm) - " + heightD);
                }



                updateButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        firstN = firstname.getText().toString().trim();
                        lastN = lastname.getText().toString().trim();
                        ageString = age.getText().toString().trim();
                        heightStr = height.getText().toString().trim();
                        NokStr = NoK.getText().toString().trim();
                        gender = spinner.getSelectedItem().toString();
                        phoneNum = NoKNum.getText().toString().trim();
                        String weightStr = weight.getText().toString().trim();


                        Map<String, Object> updateData = new HashMap<>();

                        if(!phoneNum.isEmpty())
                        {
                            if(phoneNum.length() == 10)
                            {
                                updateData.put("Next of Kin #", phoneNum);
                            }
                            else
                                {
                                    NoKNum.setError("Phone Number must have 10 digits");
                                    return;
                                }
                        }
                        if (!firstN.isEmpty())
                        {
                            updateData.put("First Name", firstN);
                        }
                        if (!lastN.isEmpty())
                        {
                            updateData.put("Last Name", lastN);
                        }
                        if (!ageString.isEmpty())
                        {
                            updateData.put("Age", ageString);
                        }

                        if (!NokStr.isEmpty())
                        {
                            updateData.put("Next of Kin", NokStr);
                        }
                        if (!heightStr.isEmpty())
                        {
                            heightF = Double.parseDouble(heightStr);
                            updateData.put("Height", heightF);
                        }

                        updateData.put("Gender", gender);

                        if (!weightStr.isEmpty())
                        {
                            Date currentDate = Calendar.getInstance().getTime();
                            DocumentReference weightDoc = fStore.collection("Users").document(userID).collection("Weights").document();
                            Map<String, Object> weightMap = new HashMap<>();
                            double weightD = Double.parseDouble(weightStr);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
                            String dateStr = dateFormat.format(currentDate);
                            weightMap.put("Date CT", currentDate);
                            weightMap.put("Weight", weightD);
                            weightMap.put("Date", Integer.parseInt(dateStr));
                            weightDoc.set(weightMap);
                        }

                        if(weightStr.isEmpty() && updateData.size() == 1)
                        {
                            Toast.makeText(PersonalInfoPage.this, "No information was updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                        }
                        else
                            {
                                documentReference.update(updateData).addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Toast.makeText(PersonalInfoPage.this, "Personal information updated successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                                    }
                                });
                             }
                    }
                });


            }
        });

        addPastWeightButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),AddPastWeightPage.class));
            }
        });

        viewWeightsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), AllWeightsPage.class));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
            }
        });


    }
}