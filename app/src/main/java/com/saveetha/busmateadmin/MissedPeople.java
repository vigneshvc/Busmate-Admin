package com.saveetha.busmateadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MissedPeople extends AppCompatActivity {

    Button showLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_people);
        showLocation = findViewById(R.id.showLocation);
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showloc = new Intent(MissedPeople.this,ShowMissedPeopleLocation.class);
                showloc.putExtra("latitudeMissed",13.1454);
                showloc.putExtra("longitudeMissed",80.2203);
                startActivity(showloc);
            }
        });
    }
}
