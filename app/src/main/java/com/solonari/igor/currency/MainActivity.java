package com.solonari.igor.currency;

import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // I know you think this should be private, but I know what I am doing
    public RecyclerView countryRecyclerView;
    public CountryAdapter countryRecyclerViewAdapter;
    public Country baseCountry = new Country("EUR", 1D);
    private List<Country> countries = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private String TAG = "main_activity";
    RequestQueue queue;
    private Handler handler;
    private Runnable runnable;
    private int delay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up list layout
        countryRecyclerView = findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        countryRecyclerView.setLayoutManager(mLayoutManager);
        countryRecyclerViewAdapter = new CountryAdapter(countries, this);
        countryRecyclerView.setAdapter(countryRecyclerViewAdapter);

        // set up the volley library to make HTTP requests
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrencies();
    }

    public void getCurrencies() {
        handler = new Handler();
        if (runnable != null) {
            runnable = null;
        }

        // Create
        runnable = new Runnable() {
            @Override
            public void run() {

                String url = "https://revolut.duckdns.org/latest?base=".concat(baseCountry.name).concat("&amount=").concat(baseCountry.value.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //Log.d(TAG, "Response: " + response.get("rates"));
                                    JSONObject rates = response.getJSONObject("rates");
                                    JSONArray keys = rates.names();

                                    // Don't use here streams or other fancy Java 8 APIs because this is the most efficient way
                                    assert keys != null;
                                    if (countries.size() > 0) {
                                        for (int i = 0; i < keys.length(); i++) {
                                            for (Country country : countries) {
                                                if (country.name.equals(keys.getString(i))) {
                                                    country.value = rates.getDouble(keys.getString(i));
                                                    // Not a good solution, rendering makes the UI laggy, needed to optimize
                                                    countryRecyclerViewAdapter.notifyItemChanged(countries.indexOf(country));
                                                }
                                            }
                                        }
                                        //countryRecyclerViewAdapter.notifyDataSetChanged();
                                    } else {
                                        countries.add(baseCountry);
                                        for (int i = 0; i < keys.length(); i++) {
                                            countries.add(new Country(keys.getString(i),
                                                    rates.getDouble(keys.getString(i))));
                                        }
                                        countryRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                    //countryRecyclerViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    Log.d(TAG, "Response: " + e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.d(TAG,"Response: " + error.toString());
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);

                // Posting the Runnable to Handler to use different thread then UI's
                handler.postDelayed(this, delay);
                delay = 1000;
            }
        };

        // start it with:
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
