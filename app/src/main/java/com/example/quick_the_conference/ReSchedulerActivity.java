package com.example.quick_the_conference;

import static android.content.Intent.ACTION_SEND;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import java.util.*;

public class ReSchedulerActivity extends AppCompatActivity {

    TextView txtDate,txtTime;
    Button btnDate,btnTime,Submit_Button,share_Btn;
    EditText roomCode;
    String ShowDate = " ",ShowTime = " ";
    FirebaseFirestore database;

    String RoomCode = " ",PreRoomCode;
    ProgressDialog pr;
    boolean b=false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_scheduler);
        Intent intent=getIntent();
        PreRoomCode=intent.getStringExtra("code");
        txtDate=findViewById(R.id.Date_View1);
        txtTime=findViewById(R.id.Time_view1);
        btnDate=findViewById(R.id.Date_BTN1);
        btnTime=findViewById(R.id.Time_Button1);
        share_Btn=findViewById(R.id.SHARE_Btn1);
        roomCode=findViewById(R.id.Room_Code1);
        pr=new ProgressDialog(ReSchedulerActivity.this);
        pr.setTitle("Please wait...!");
        pr.setMessage("Just a moment ....!");
        roomCode.setText(PreRoomCode);
        database = FirebaseFirestore.getInstance();
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        Submit_Button=findViewById(R.id.Submit_Btn1);
        Submit_Button.setOnClickListener(v -> {
            pr.show();
            RoomCode=roomCode.getText().toString().trim();
            b=true;
           dataupdate();
        });


        share_Btn.setOnClickListener(v -> {

            if (RoomCode.length() > 1 && ShowDate.length() >=8 && ShowTime.length() >=5 ) {
                Intent intent1 = new Intent();
                intent1.setAction(ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, "Quick The Conference : Room Code:- " + RoomCode + "    Date:-" + ShowDate + "       Time:-" + ShowTime);
                intent1.setType("text/plain");
                startActivity(intent1);
            }
            else if(RoomCode.length()==1 || ShowDate.length()==1 || ShowDate.length()==1)
            {
                Toast.makeText(this, "Submit your schedule details!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void dataupdate() {

        if (RoomCode.length() > 1 && ShowDate.length() >=8 && ShowTime.length() >=5 )
        {
            pr.dismiss();
            Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("Date", ShowDate);
        userDetails.put("Time", ShowTime);
        userDetails.put("RoomCode", RoomCode);

        database.collection("UsersScheduler")
                .whereEqualTo("RoomCode", PreRoomCode)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            database.collection("UsersScheduler")
                                    .document(documentId)
                                    .update(userDetails)
                                    .addOnSuccessListener(documentReference -> {
                                        pr.dismiss();
                                        Toast.makeText(ReSchedulerActivity.this, "Your meeting ReScheduled Successfully", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        pr.dismiss();
                                        Toast.makeText(ReSchedulerActivity.this, "Error! Meeting not Scheduled due to - " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {

                            Toast.makeText(ReSchedulerActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
        else
        { pr.dismiss();
            Toast.makeText(this, "Above fields can't be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDate(){

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int date=calendar.get(Calendar.DATE);


        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date)
            {
                String showDate=date+"/"+(month+1)+"/"+year;
                System.out.println("storing date:"+showDate);
                ShowDate=showDate.trim();
                txtDate.setText(showDate);
            }

        },year,month,date);

        datePickerDialog.show();
    }
    private void setTime(){

        Calendar calendar=Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR);
        int min=calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int min) {
                String showTime=hour+" : "+min;
                ShowTime=showTime.trim();
                txtTime.setText(showTime);
            }
        },hour,min,false);
        timePickerDialog.show();
    }


}