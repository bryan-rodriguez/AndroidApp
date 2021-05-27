package com.example.phms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DoctorPage extends AppCompatActivity
{
    TextView doctorsAvailable, tvDoctor;
    String userID;
    Button addDoctor, homePage;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    LinearLayout ll;
    int numOfLines;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_page);

        numOfLines = 1;
        addDoctor = findViewById(R.id.addDoctorButton);
        doctorsAvailable = findViewById(R.id.numOfDoctors);
        ll = findViewById(R.id.layout);
        homePage = findViewById(R.id.homepage);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        CollectionReference doctorCR = fStore.collection("Users").document(userID).collection("Doctors");

        doctorCR.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                List<DocumentSnapshot> doctorDocuments = queryDocumentSnapshots.getDocuments();
                if(!doctorDocuments.isEmpty())
                {
                    doctorsAvailable.setText(queryDocumentSnapshots.size() + " Doctors Available");
                    for(DocumentSnapshot ds : doctorDocuments)
                    {
                        tvDoctor = new TextView(DoctorPage.this);
                        tvDoctor.setId(ViewCompat.generateViewId());
                        tvDoctor.setText(ds.getString("Name"));
                        tvDoctor.setTextSize(20);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,70,0,0);
                        tvDoctor.setLayoutParams(params);
                        tvDoctor.setGravity(Gravity.CENTER);

                        tvDoctor.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(), EditDoctorPage.class);
                                intent.putExtra("Document ID", ds.getId());
                                startActivity(intent);
                            }
                        });

                        tvDoctor.setOnLongClickListener(new View.OnLongClickListener()
                        {
                            @Override
                            public boolean onLongClick(View v)
                            {
                                AlertDialog.Builder delete = new AlertDialog.Builder(DoctorPage.this);
                                delete.setTitle("Delete");
                                delete.setMessage("Are you sure you want to delete " + ds.getString("Name") + "?");
                                delete.setPositiveButton("delete", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        DocumentReference documentReference = ds.getReference();
                                        documentReference.delete();
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                                delete.setNegativeButton("cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });
                                delete.show();
                                return false;
                            }
                        });
                        ll.addView(tvDoctor, numOfLines);
                        numOfLines++;
                    }


                }

            }
        });

        addDoctor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), AddDoctorPage.class));
            }
        });

        homePage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
            }
        });





    }
}