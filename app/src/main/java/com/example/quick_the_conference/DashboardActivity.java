package com.example.quick_the_conference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import org.jitsi.meet.sdk.*;
import java.net.*;
import java.text.*;
import java.time.LocalTime;
import java.util.*;

public class DashboardActivity extends AppCompatActivity {
    EditText codeBox;
    Button joinBtn, demoBtn, scheduleBtn, rescheduleBtn;
    FirebaseFirestore database;
    String time = " ", date = " ", hr, min, RoomCode = " ", crdate, crcode = " ";
    String arr[] = new String[3];
    int Date1, Month1, Year1, Date2, Month2, Year2;
    Date Dcr, Drt;

    boolean x1 = false, x2 = false;
    ProgressDialog pr;
    int h1, m1, h2, m2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        codeBox = findViewById(R.id.CodeBox);
        joinBtn = findViewById(R.id.JOINBTN);
        database = FirebaseFirestore.getInstance();
        pr = new ProgressDialog(DashboardActivity.this);
        pr.setTitle("please wait for a while!");
        pr.setMessage("Just a moment!");
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(true).build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        joinBtn.setOnClickListener(this::onClick);

        demoBtn = findViewById(R.id.demoBtn);
        demoBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        });

        scheduleBtn = findViewById(R.id.ScheduleBtn);
        scheduleBtn.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SchedulerActivity.class));

                }
        );

        rescheduleBtn = findViewById(R.id.ReScheduleBtn);
        rescheduleBtn.setOnClickListener(v -> {
                    if (codeBox.getText().toString().length() > 1) {
                        OnRescheduleClick(v);
                        if (x2) {
                            Intent intent = new Intent(DashboardActivity.this, ReSchedulerActivity.class);
                            intent.putExtra("code", codeBox.getText().toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Your meet is not scheduled!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please Enter the roomcode first!", Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppExit();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void AppExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void deleteData(String roomcode) {
        database.collection("UsersScheduler")
                .whereEqualTo("Date", date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            database.collection("UsersScheduler")
                                    .document(documentID)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                        }
                                    });
                        }
                    }
                });

    }

    void calDrt(String date) throws Exception {
        SimpleDateFormat formmatter = new SimpleDateFormat("dd/MM/yyyy");
        Drt = formmatter.parse(date);
    }

    private void onClick(View v) {
        pr.show();
        crcode = Objects.requireNonNull(codeBox.getText().toString().trim());
        if (crcode.length() > 1) {
            database.collection("UsersScheduler")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            pr.dismiss();
                            LocalTime lt = LocalTime.now();
                            String l1 = lt.toString().trim();
                            hr = l1.substring(0, 2).trim();
                            h1 = Integer.parseInt(hr);
                            System.out.println("cr hour:-" + h1);
                            min = l1.substring(3, 5).trim();
                            m1 = Integer.parseInt(min);
                            System.out.println("cr min:-" + m1);
                            SimpleDateFormat fomatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = new Date();
                            crdate = fomatter.format(date1).trim();
                            try {
                                Dcr = fomatter.parse(crdate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            System.out.println("Dcr=" + Dcr);
                            arr = crdate.split("/");
                            Date2 = Integer.parseInt(arr[0]);
                            Month2 = Integer.parseInt(arr[1]);
                            Year2 = Integer.parseInt(arr[2]);
                            System.out.println("cr code:-" + crcode);
                            System.out.println("cr date:-" + crdate);
                            System.out.println("cr code length" + crcode.length());
                            System.out.println("cr date length:-" + crdate.length());


                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshotList) {

                                RoomCode = snapshot.getString("RoomCode");
                                System.out.println("Room code:-" + RoomCode);
                                date = snapshot.getString("Date");


                                try {
                                    calDrt(date);
                                    if (Drt.before(Dcr))
                                        deleteData(RoomCode);
                                    System.out.println("Value=" + Drt.before(Dcr));
                                    System.out.println("Drt=" + Drt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                if ((RoomCode.equals(crcode)) == true) {
                                    x2 = true;
                                    time = snapshot.getString("Time");
                                    System.out.println("rt date:-" + date);
                                    System.out.println("rt code length" + RoomCode.length());
                                    System.out.println("rt date length:-" + date.length());
                                    System.out.println("time:" + time);
                                    System.out.println("rt code:-" + RoomCode);
                                    h2 = Integer.parseInt(time.substring(0, 2).trim());
                                    m2 = Integer.parseInt(time.substring(4).trim());

                                    System.out.println("rt hour:-" + h2);
                                    System.out.println("rt min:-" + m2);
                                    arr = date.split("/");
                                    Date2 = Integer.parseInt(arr[0]);
                                    Month2 = Integer.parseInt(arr[1]);
                                    Year2 = Integer.parseInt(arr[2]);
                                    System.out.println("x value before=" + x1);
                                    if (h1 == h2 && m1 >= m2 && m1 <= (m2 + 5) && Drt.equals(Dcr)) {
                                        x1 = true;
                                        System.out.println("x value=" + x1);
                                    }
                                }
                                System.out.println("------------------------------------------------------------------------------");

                            }
                            if (x1) {

                                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                        .setRoom(crcode)
                                        .setFeatureFlag("invite.enabled", false)
                                        .setFeatureFlag("help.enabled", false)
                                        .setAudioMuted(true)
                                        .setAudioMuted(false)
                                        .setWelcomePageEnabled(true)
                                        .build();
                                JitsiMeetActivity.launch(DashboardActivity.this, options);
                            } else {
                                Toast.makeText(DashboardActivity.this, "The meet which you are going to enter is not scheduled or might be not the proper time!", Toast.LENGTH_LONG).show();
                            }


                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pr.dismiss();
                            Toast.makeText(DashboardActivity.this, "data is not retriving", Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            pr.dismiss();
            Toast.makeText(this, "Please enter the room code!", Toast.LENGTH_SHORT).show();
        }
    }

    private void OnRescheduleClick(View v) {
        database.collection("UsersScheduler")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        crcode = Objects.requireNonNull(codeBox.getText().toString().trim());
                        for (DocumentSnapshot snapshot : snapshotList) {
                            RoomCode = snapshot.getString("RoomCode");
                            if ((RoomCode.equals(crcode)) == true) {
                                x2 = true;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pr.dismiss();
                        Toast.makeText(DashboardActivity.this, "data is not retriving", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}