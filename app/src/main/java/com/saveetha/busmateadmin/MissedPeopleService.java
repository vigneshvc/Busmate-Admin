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

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MissedPeopleService extends Service {


    String CHANNEL_ID="com.saveetha.busmateadmin.missedpeopleservice";
    String CHANNEL_NAME="Busmate Admin";
    String TAG = "MissedPeopleService";
    Handler handler;
    SharedPreferences sp;
    String busID;
    public String PREFERENCENAME = "busmateAdmin";
    public MissedPeopleService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.getString("ACTIONACCEPT","false").equals("true")){
                String StudentID = extras.getString("STUDID");
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
                        params.put("studid",busID+"");
                        params.put("accept","true");
                        return params;
                    }
                };

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            else{
                String StudentID = extras.getString("STUDID");
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
                        params.put("studid",busID+"");
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
                                                                    PendingIntent.getService(MissedPeopleService.this, 0, snoozeIntent, 0);
                                                            Intent snoozeIntent2 = new Intent(MissedPeopleService.this, MissedPeopleService.class);
                                                            snoozeIntent2.putExtra("ACTIONACCEPT","false");
                                                            snoozeIntent2.putExtra("STUDID",studentID);
                                                            //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
                                                            PendingIntent snoozePendingIntent2 =
                                                                    PendingIntent.getService(MissedPeopleService.this, 0, snoozeIntent2, 0);
                                                            NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                                                                NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
                                                                channel.setDescription("To display prompts to accept or deny requests");
                                                                mNotificationManager.createNotificationChannel(channel);
                                                            }
                                                            Intent MissedPeopleActivityIntent = new Intent(MissedPeopleService.this,MissedPeople.class);
                                                            PendingIntent pendingContentIntent = PendingIntent.getActivity(MissedPeopleService.this,0,MissedPeopleActivityIntent,0);
                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MissedPeopleService.this, CHANNEL_ID)
                                                                    .setSmallIcon(R.drawable.busimage)
                                                                    .setContentIntent(pendingContentIntent)
                                                                    .setContentTitle("Missed the Bus")
                                                                    .setContentText("Accept to stop at "+stopname+" ?")
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                    .addAction(R.drawable.busimage, "ACCEPT",
                                                                            snoozePendingIntent)
                                                                    .addAction(R.drawable.busimage, "DENY",
                                                                            snoozePendingIntent2);
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
                                            params.put("busid",busID+"");
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
