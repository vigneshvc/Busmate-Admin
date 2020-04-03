package com.saveetha.busmateadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class ActionDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    Button adduser,synclocation,busmissedpeople;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_dashboard);
        adduser = findViewById(R.id.addUserButton);
        synclocation = findViewById(R.id.locationSyncButton);
        busmissedpeople = findViewById(R.id.busMissedPeople);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(ActionDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ActionDashboardActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 100);
        }
        adduser.setOnClickListener(this);
        synclocation.setOnClickListener(this);;
        busmissedpeople.setOnClickListener(this);
        stopService(new Intent(this,LocationService.class));
        synclocation.setText("START LOCATION SYNC");
        Toast.makeText(this,"Location sync stopped",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==adduser.getId()){
            startActivity(new Intent(ActionDashboardActivity.this,AddUser.class));
        }
        if(v.getId()==synclocation.getId()){




            if(synclocation.getText() == "START LOCATION SYNC"){
                Calendar timenow = Calendar.getInstance();
                Calendar fiveforty = Calendar.getInstance();
                Calendar eightfifteen = Calendar.getInstance();
                fiveforty.set(Calendar.HOUR,5);
                fiveforty.set(Calendar.MINUTE,40);
                fiveforty.set(Calendar.SECOND,0);
                fiveforty.set(Calendar.AM_PM,Calendar.AM);
                eightfifteen.set(Calendar.HOUR,8);
                eightfifteen.set(Calendar.MINUTE,15);
                eightfifteen.set(Calendar.SECOND,0);
                eightfifteen.set(Calendar.AM_PM,Calendar.AM);
                if(timenow.compareTo(fiveforty)>0 &&timenow.compareTo(eightfifteen)<0){
                    startService(new Intent(this,LocationService.class));
                    synclocation.setText("STOP LOCATION SYNC");
                }
                else{
                    Toast.makeText(this,"Improper time",Toast.LENGTH_LONG).show();
                }
            }else{
                stopService(new Intent(this,LocationService.class));
                synclocation.setText("START LOCATION SYNC");


            }
        }
        if(v.getId()==busmissedpeople.getId()){
            startActivity(new Intent(this,MissedPeople.class));
        }
    }
}
