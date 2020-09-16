package com.saveetha.busmateadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String TAG = "Adduser";
    EditText usermail,userpass, username,usercontact;
    Button addupdatebutton;
    Spinner busSpinner,stopSpinner;
    String BusURL= "https://apexcoders.in/busmate/getbuses.php";
    String StopURL = "https://apexcoders.in/busmate/getstops.php";
    ArrayList<String> buses,stops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        usermail = findViewById(R.id.usermailEditText);
        userpass = findViewById(R.id.userpassEditText);
        username = findViewById(R.id.usernameEditText);
        usercontact = findViewById(R.id.usercontactEditText);
        addupdatebutton = findViewById(R.id.addupdatebutton);
        addupdatebutton.setOnClickListener(this);
        busSpinner = findViewById(R.id.busSpinner);
        stopSpinner = findViewById(R.id.stopSpinner);
        buses = new ArrayList<>();
        stops = new ArrayList<>();
        loadBusSpinner();
        loadStopSpinner();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadStopSpinner() {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, StopURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    Log.e(TAG,response);
                    if(jsonObject.getString("status").equals("success")){
                        int total=jsonObject.getInt("total");
                        for(int i=0;i<total;i++){
                            String stop = jsonObject.getString("Stop"+i);
                            stops.add(stop);
                        }
                    }
                    stopSpinner.setAdapter(new ArrayAdapter<String>(AddUser.this, android.R.layout.simple_spinner_dropdown_item, stops));
                }catch (Exception e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
    private void loadBusSpinner() {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, BusURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        int total=jsonObject.getInt("total");
                        for(int i=0;i<total;i++){
                            String stop = jsonObject.getString("Bus"+i);
                            buses.add(stop);
                        }
                    }
                    busSpinner.setAdapter(new ArrayAdapter<String>(AddUser.this, android.R.layout.simple_spinner_dropdown_item, buses));
                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==addupdatebutton.getId()){
            final String bus = busSpinner.getSelectedItem().toString();
            final String stop = stopSpinner.getSelectedItem().toString();
            final String usermailid = usermail.getText().toString();
            final String userpassword = userpass.getText().toString();
            final String username = this.username.getText().toString();
            final String usercontactno = usercontact.getText().toString();
            if(bus.equals("") || stop.equals("") || usermailid.equals("") || userpassword.equals("")){
                addupdatebutton.setText("Try Again!");
            }
            else{
                final String url = getString(R.string.server_address)+"addupdateuser.php";
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
                                    if(status.equals("success")){
                                        Toast.makeText(AddUser.this,"User Inserted/Updated",Toast.LENGTH_LONG).show();
                                        usermail.setText("");
                                        userpass.setText("");
                                    }else{
                                        addupdatebutton.setText("Server Error!Try Again");
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
                        params.put("mailid",usermailid);
                        params.put("password", userpassword);
                        params.put("busid",bus);
                        params.put("stopid",stop);
                        params.put("username",username);
                        params.put("usercontact",usercontactno);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
