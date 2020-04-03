package com.saveetha.busmateadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username,password;
    SharedPreferences sp ;
    public String PREFERENCENAME = "busmateAdmin";
    Button login;
    String user,pass;
    String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.adminusername);
        login = findViewById(R.id.loginbutton);
        password = findViewById(R.id.adminpassword);
        login.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        sp = getSharedPreferences(PREFERENCENAME,MODE_PRIVATE);
        if(sp.getBoolean("loggedin",false)){
            startActivity(new Intent(this,ActionDashboardActivity.class));
            finish();
        }else{

        }

    }
    @Override
    protected void onPause() {

        // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(username.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(password.getWindowToken(), 0);

        super.onPause();
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == login.getId()){
            Log.i(TAG,"Login button clicked!");
            user = username.getText().toString();
            pass = password.getText().toString();
            Log.i(TAG,"username entered - "+user+" password entered - "+pass);
            final String url = getString(R.string.server_address)+"/adminlogin.php";
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
                                if(status.equals("success")){
                                    sp.edit().putBoolean("loggedin",true).apply();
                                    sp.edit().putString("user",jb.getString("username")).apply();
                                    sp.edit().putString("stop_id",jb.getString("stop_id")).apply();
                                    startActivity(new Intent(MainActivity.this,ActionDashboardActivity.class));
                                    finish();
                                }else{
                                    //failed login
                                    login.setText(R.string.loginfailed);
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
                    params.put("username",user);
                    params.put("password", pass);

                    return params;
                }
            };
            queue.add(stringRequest);

        }
    }
}
