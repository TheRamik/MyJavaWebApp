package com.example.hayleytse.project4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etEmail = findViewById(R.id.email);
        final EditText etPassword = findViewById(R.id.password);
    }

    public void loginDatabase(View view){

        //
        final EditText etEmail = findViewById(R.id.email);
        final EditText etPassword = findViewById(R.id.password);

        final Map<String, String> params = new HashMap<String, String>();

        String sEmail = etEmail.getText().toString();
        String sPassword = etPassword.getText().toString();


        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;
        String url = "http://18.144.8.219:8080/cs122bprojectM/Login?username="
                     + sEmail + "&password=" + sPassword;

        Log.d("email", sEmail);
        Log.d("pw", sPassword);
        Log.d("url", url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);
                        try {
                            JSONObject loginData = new JSONObject(response);
                            String loginStatus = loginData.getString("status");
                            String loginMessage = loginData.getString("message");
                            Log.d("status", loginStatus);
                            Log.d("message", loginMessage);

                            if(loginStatus.equals("success")) {
                                //go to search activity
                                Log.d("success", "login success");
                                Intent searchIntent = new Intent(LoginActivity.this, SearchActivity.class);
                                LoginActivity.this.startActivity(searchIntent);

                            } else {
                                Log.d("fail", "login failed");
                                ((TextView) findViewById(R.id.http_response)).setText(loginMessage);
                            }
                        } catch (final JSONException e) {
                            Log.d("error", "Json parsing error: " + e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(postRequest);


        return ;
    }
}
