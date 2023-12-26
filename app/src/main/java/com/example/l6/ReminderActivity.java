package com.example.l6;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderActivity extends AppCompatActivity {

    private int reminderId;
    private TextView reminderDateTime, reminderTitle, reminderDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        reminderDateTime = findViewById(R.id.reminderActivityDateTime);
        reminderTitle = findViewById(R.id.reminderActivityTitle);
        reminderDescription = findViewById(R.id.reminderActivityDescription);

        reminderId = getIntent().getIntExtra("reminderId", 0);
        reminderDateTime.setText(getIntent().getStringExtra("reminderDateTime"));
        reminderTitle.setText(getIntent().getStringExtra("reminderTitle"));
        reminderDescription.setText(getIntent().getStringExtra("reminderDescription"));
    }
}