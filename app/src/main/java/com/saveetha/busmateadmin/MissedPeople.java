package com.saveetha.busmateadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MissedPeople extends AppCompatActivity {

    TextView missedStudentName,missedStudentLocation,titleMissed;
    Button showLocation,acceptbtn,denybtn;
    double latt=13,longg=80;
    public String PREFERENCENAME = "busmateAdmin";
    String busID,stopID,studId;
    final String TAG = "MISSED PEOPLE ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_people);
        busID = getSharedPreferences(PREFERENCENAME,MODE_PRIVATE).getString("bus_id","");
        showLocation = findViewById(R.id.showLocation);
        missedStudentName = findViewById(R.id.missedStudentName);
        missedStudentLocation = findViewById(R.id.missedLocation);
        titleMissed = findViewById(R.id.textViewMissed);
        acceptbtn = findViewById(R.id.acceptButton);
        denybtn = findViewById(R.id.denybutton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = getString(R.string.server_address)+"missedbus.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,"RESPONSE:"+response);
                        try {
                            JSONObject jb = new JSONObject(response);
                            String found =jb.getString("found");
                            Log.i(TAG,found+" -- found text");
                            if(found.equals("true")){
                                String studentID = jb.getString("studentID");
                                studId=studentID;
                                String stopname = jb.getString("stopName");
                                stopID = jb.getString("stopID");
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                final String url = getString(R.string.server_address)+"getStopLocations.php";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.i(TAG,"RESPONSE:"+response);
                                                try {
                                                    JSONObject jb = new JSONObject(response);
                                                    String status =jb.getString("status");
                                                    if(status.equals("success")){
                                                        latt=Double.parseDouble(jb.getString("latitude"));
                                                        longg=Double.parseDouble(jb.getString("longitude"));
                                                        Log.i(TAG,"Stop Location Updated!");
                                                    }
                                                } catch (Exception e) {
                                                    Log.e(TAG, e.toString());
                                                }
                                                //result.setText(url);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(TAG,error.toString());
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("stopid",stopID);
                                        return params;
                                    }
                                };

// Add the request to the RequestQueue.
                                queue.add(stringRequest);


                                titleMissed.setText("BUS MISSED PEOPLE");
                                showLocation.setVisibility(View.VISIBLE);
                                missedStudentName.setVisibility(View.VISIBLE);
                                missedStudentName.setText(studentID);
                                missedStudentLocation.setVisibility(View.VISIBLE);
                                missedStudentLocation.setText(stopname);
                                acceptbtn.setVisibility(View.VISIBLE);
                                denybtn.setVisibility(View.VISIBLE);
                            }else{

                                titleMissed.setText("NO BUS MISSED PEOPLE");
                                showLocation.setVisibility(View.INVISIBLE);
                                missedStudentName.setVisibility(View.INVISIBLE);
                                missedStudentLocation.setVisibility(View.INVISIBLE);
                                acceptbtn.setVisibility(View.INVISIBLE);
                                denybtn.setVisibility(View.INVISIBLE);
                                Log.i(TAG,"found is false");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        //result.setText(url);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("busid",busID);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissedPeople.this,MissedPeopleService.class);
                intent.putExtra("ACTIONACCEPT","true");
                intent.putExtra("STUDID",studId);
                intent.putExtra("BUSID",busID);
                startService(intent);
            }
        });
        denybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissedPeople.this,MissedPeopleService.class);
                intent.putExtra("ACTIONACCEPT","false");
                intent.putExtra("STUDID",studId);
                intent.putExtra("BUSID",busID);
                startService(intent);
                finish();
            }
        });
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showloc = new Intent(MissedPeople.this,ShowMissedPeopleLocation.class);
                showloc.putExtra("latitudeMissed",latt);
                showloc.putExtra("longitudeMissed",longg);
                startActivity(showloc);
                finish();
            }
        });
    }
}
