package com.saveetha.busmateadmin;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class LocationService extends Service implements LocationListener {
    final String CHANNEL_ID = "ForegroundServiceChannelLocationUpdater";
    LocationManager locationManager;
    String TAG = "Busmate Admin Location Service";
    String user,lat,longg,busid;
    public String PREFERENCENAME = "busmateAdmin";

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundApi();
        }
        SharedPreferences sp = getSharedPreferences(PREFERENCENAME,MODE_PRIVATE);
        user = sp.getString("user","");
        busid = sp.getString("stop_id","");
        if(user.equals("") || busid.equals("")){
            sp.edit().putString("user","").apply();
            sp.edit().putString("stop_id","").apply();
            sp.edit().putBoolean("islogged",false).apply();
            stopForeground(true);
            stopSelf();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void startForegroundApi() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Busmate Admin", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager mgr = getSystemService(NotificationManager.class);
        mgr.createNotificationChannel(serviceChannel);
        Intent notificationIntent = new Intent(this, this.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle("BusMate Admin").setContentText("connected to server").setTicker("syncing!").build();
        startForeground(11129, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        stopForeground(true);
        Toast.makeText(this,"Location Update Stopped",Toast.LENGTH_LONG);
        stopSelf();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onLocationChanged(final Location location) {
        final String url = getString(R.string.server_address)+"/updateadminlocation.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        isLogged = response.equals("true"); //dummy for now
                        Log.i(TAG,"RESPONSE:"+response);
                        try {
                            JSONObject jb = new JSONObject(response);
                            String status = jb.getString("status");
                            Log.i(TAG,"Status returned - "+status);
                            if(!status.equals("success")){
                                //failed
                                Toast.makeText(LocationService.this,"Server Error",Toast.LENGTH_SHORT).show();
                                stopForeground(true);
                                stopSelf();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"error-"+error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("busid", busid);
                params.put("lat", location.getLatitude()+"");
                params.put("long", location.getLongitude()+"");
                params.put("user", user);
                return params;
            }
        };

        queue.add(stringRequest);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
