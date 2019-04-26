package com.example.hayleytse.project4;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    ArrayList<MovieDataModel> movieModels;
    ListView listView;
    Button nextBtn, prevBtn;
    private static CustomAdaptor adaptor;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = findViewById(R.id.mList);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);

        movieModels = new ArrayList<>();
    }

    public void searchDatabase(View view){

        final EditText etQuery = findViewById(R.id.query);
        final Map<String, String> params = new HashMap<String, String>();
        String sQuery = etQuery.getText().toString();

        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;

        String[] splitQuery = sQuery.split(" ");
        String url = "http://18.144.8.219:8080/cs122bprojectM/Search?search=";
        //ex: Search?search=death+s

        for(int i = 0; i < splitQuery.length; i++) {
            if(splitQuery.length == 1) {
                url += sQuery;
            } else {
                url += splitQuery[i] + "+";
            }
        }

        url += "&page=" + currentPage;

        Log.d("query", sQuery);
        Log.d("url", url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);
                        try {
                            //show movie list
                            movieModels.clear();
                            JSONArray movies = new JSONArray(response);

                            for(int i = 0; i < movies.length(); i++) {
                                JSONObject m = movies.getJSONObject(i);
                                String m_id = m.getString("movie_id");
                                String m_title = m.getString("movie_title");
                                String m_year = String.valueOf(m.getInt("movie_year"));
                                String m_dir = m.getString("movie_director");
                                String m_genres = m.getString("genre_list");
                                String m_stars = m.getString("star_list");

                                movieModels.add(new MovieDataModel(m_id, m_title, m_year, m_dir, m_genres, m_stars));
                            }

                            adaptor = new CustomAdaptor(movieModels, getApplicationContext());

                            listView.setAdapter(adaptor);

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

    public void nextButton(View view) {
        currentPage += 1;
        searchDatabase(view);
        //listView.setAdapter(adaptor);
    }

    public void prevButton(View view) {
        currentPage -= 1;
        searchDatabase(view);
        //listView.setAdapter(adaptor);
    }
}
