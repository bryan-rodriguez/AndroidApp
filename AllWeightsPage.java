package com.example.phms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AllWeightsPage extends AppCompatActivity
{
    TextView numOfWeightsTV;
    LinearLayout ll;
    String userID;
    Button personalInfo;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_weights_page);

        ll = findViewById(R.id.layout);
        personalInfo = findViewById(R.id.personalInfoButton);
        numOfWeightsTV = findViewById(R.id.numOfWeightsTV);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();



        CollectionReference weightDoc = fStore.collection("Users").document(userID).collection("Weights");

        weightDoc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() != 0)
                {
                    int numOfLines = 2;
                    numOfWeightsTV.setText(queryDocumentSnapshots.size() + " Weights Available");
                    List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot A : ds) {
                        String tvdateStr = String.valueOf(A.getLong("Date"));
                        String tvDateYearStr = tvdateStr.substring(0, 4);
                        String tvDateMonthStr = tvdateStr.substring(4, 6);
                        String tvDateDayStr = tvdateStr.substring(6, 8);
                        String date = tvDateYearStr + "/" + tvDateMonthStr + "/" + tvDateDayStr;

                        TextView tvDoctor = new TextView(AllWeightsPage.this);
                        tvDoctor.setId(ViewCompat.generateViewId());
                        tvDoctor.setText("Weight - " + A.getDouble("Weight") + "\nDate - " + date);
                        tvDoctor.setTextSize(20);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 70, 0, 0);
                        tvDoctor.setLayoutParams(params);
                        tvDoctor.setGravity(Gravity.CENTER);

                        tvDoctor.setOnLongClickListener(new View.OnLongClickListener()
                        {
                            @Override
                            public boolean onLongClick(View v)
                            {
                                AlertDialog.Builder delete = new AlertDialog.Builder(AllWeightsPage.this);
                                delete.setTitle("Delete");
                                delete.setMessage("Are you sure you want to delete this weight?");
                                delete.setPositiveButton("delete", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        DocumentReference documentReference = A.getReference();
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

        personalInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), PersonalInfoPage.class));
            }
        });
    }
}