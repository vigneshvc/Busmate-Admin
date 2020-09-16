package com.saveetha.busmateadmin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MissedPeopleService extends Service {


    public String PREFERENCENAME = "busmateAdmin";
    String CHANNEL_ID="com.saveetha.busmateadmin.missedpeopleservice";
    String CHANNEL_NAME="Busmate Admin";
    String TAG = "MissedPeopleService";
    Handler handler;
    SharedPreferences sp;
    String busID;
    public MissedPeopleService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        busID = getSharedPreferences(PREFERENCENAME,MODE_PRIVATE).getString("bus_id","");
        Bundle extras = intent.getExtras();
        if(extras != null){
            Log.e(TAG,"Extras -- "+extras.getString("ACTIONACCEPT"));
            if(extras.getString("ACTIONACCEPT").equals("true")){
                final String StudentID = extras.getString("STUDID");
                final String busid = extras.getString("BUSID");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = getString(R.string.server_address)+"acceptmissedbus.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG,"RESPONSE:"+response);
                                try {
                                    JSONObject jb = new JSONObject(response);

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
                        params.put("studid",StudentID);
                        params.put("busid",busid);
                        params.put("accept","true");
                        return params;
                    }
                };

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            else{
                final String StudentID = extras.getString("STUDID");
                final String busid = extras.getString("BUSID");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = getString(R.string.server_address)+"acceptmissedbus.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG,"RESPONSE:"+response);
                                try {
                                    JSONObject jb = new JSONObject(response);

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
                        params.put("studid",StudentID);
                        params.put("busid",busid);
                        params.put("accept","false");
                        return params;
                    }
                };

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            stopSelf();
        }
        sp = getSharedPreferences(PREFERENCENAME,MODE_PRIVATE);
        busID = sp.getString("bus_id","");

        handler = new Handler();
        handler.postDelayed(new Runnable(){

                                @Override
                                public void run() {
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
                                                            String stopname = jb.getString("stopName");

                                                            Intent snoozeIntent = new Intent(MissedPeopleService.this, MissedPeopleService.class);
                                                            snoozeIntent.putExtra("ACTIONACCEPT","true");
                                                            snoozeIntent.putExtra("STUDID",studentID);
                                                            Log.i(TAG,"Performing notification things");
                                                            //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
                                                            PendingIntent snoozePendingIntent =
                                                                    PendingIntent.getService(MissedPeopleService.this, new Random().nextInt(), snoozeIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                                            Intent snoozeIntent2 = new Intent(MissedPeopleService.this, MissedPeopleService.class);
                                                            snoozeIntent2.putExtra("ACTIONACCEPT","fALse");
                                                            snoozeIntent2.putExtra("STUDID",studentID);
                                                            //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
                                                            PendingIntent snoozePendingIntent2 =
                                                                    PendingIntent.getService(MissedPeopleService.this, new Random().nextInt(), snoozeIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
                                                            NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                                                                NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW);
                                                                channel.setDescription("To display prompts to accept or deny requests");
                                                                mNotificationManager.createNotificationChannel(channel);
                                                            }
                                                            Intent MissedPeopleActivityIntent = new Intent(MissedPeopleService.this,MissedPeople.class);
                                                            PendingIntent pendingContentIntent = PendingIntent.getActivity(MissedPeopleService.this,new Random().nextInt(),MissedPeopleActivityIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MissedPeopleService.this, CHANNEL_ID)
                                                                    .setSmallIcon(R.drawable.busimage)
                                                                    .setContentIntent(pendingContentIntent)
                                                                    .setContentTitle("Missed the Bus")
                                                                    .setContentText("Accept to stop at "+stopname+" ?")
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                    .setAutoCancel(true);
                                                                    //.addAction(R.drawable.busimage, "DENY",SnoozePendingIntent2)
                                                                    //.addAction(R.drawable.busimage, "ACCEPT",snoozePendingIntent2)

                                                            mNotificationManager.notify(0,builder.build());
                                                            Log.i(TAG,"Displayed Notif!");
                                                        }else{
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
                                    handler.postDelayed(this, 10000);
                                }
                            },
                10000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
