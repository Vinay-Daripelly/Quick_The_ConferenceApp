package com.example.quick_the_conference;

import static android.content.Intent.ACTION_SEND;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import java.util.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class SchedulerActivity extends AppCompatActivity {

    TextView txtDate,txtTime;
    Button btnDate,btnTime,Submit_Button,share_Btn;
    EditText roomCode;
    String ShowDate = " ",ShowTime = " ";
    FirebaseFirestore database;
    boolean b=false;
    String RoomCode = " ";
    ProgressDialog pr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        txtDate=findViewById(R.id.Date_View);
        txtTime=findViewById(R.id.Time_view);
        btnDate=findViewById(R.id.Date_BTN);
        btnTime=findViewById(R.id.Time_Button);
        share_Btn=findViewById(R.id.SHARE_Btn);
        roomCode=findViewById(R.id.Room_Code);
        pr=new ProgressDialog(SchedulerActivity.this);
        pr.setTitle("Please wait...!");
        pr.setMessage("Just a moment ....!");

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
        Submit_Button=findViewById(R.id.Submit_Btn);
        Submit_Button.setOnClickListener(v -> {

            RoomCode=roomCode.getText().toString().trim();
            b=true;
            if(RoomCode.length()>1 && ShowTime.length()>=5 && ShowDate.length()>=8) {
            Map<String, Object> users = new HashMap<>();
                pr.show();
                users.put("Date", ShowDate);
                users.put("Time", ShowTime);
                users.put("RoomCode", RoomCode);

                // Add a new document with a generated ID
                database.collection("UsersScheduler")
                        .add(users)
                        .addOnSuccessListener(documentReference -> {
                            pr.dismiss();
                            Toast.makeText(SchedulerActivity.this, "Your meeting Scheduled Successfully", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            pr.dismiss();
                            Toast.makeText(SchedulerActivity.this, "Error! Meeting not Scheduled due to - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
            else
            {
                Toast.makeText(this, "Above fields can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });


        share_Btn.setOnClickListener(v -> {

            if (RoomCode.length() > 1 && ShowDate.length() >=8 && ShowTime.length() >=5 ) {
                Intent intent = new Intent();
                intent.setAction(ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Quick The Conference : Room Code:- " + RoomCode + "    Date:-" + ShowDate + "       Time:-" + ShowTime);
                intent.setType("text/plain");
                startActivity(intent);
            }
            else if(RoomCode.length()==1 || ShowDate.length()==1 || ShowDate.length()==1)
            {
                Toast.makeText(this, "Submit your schedule details!", Toast.LENGTH_SHORT).show();
            }

        });
       
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