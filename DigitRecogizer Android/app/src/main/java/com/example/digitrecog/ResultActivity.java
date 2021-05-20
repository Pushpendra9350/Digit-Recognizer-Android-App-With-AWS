package com.example.digitrecog;
/**
 * @Author: Pushpendra Kumar
 * This activity will show you reslts and save your result
 * for further training the data
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity{

    // Variables which are used in this class
    private TextView response,instruction;
    private Button wrong, correct, sendResult;
    private Spinner spinner;
    private String selected;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String responseString = intent.getStringExtra("response");
        url = intent.getStringExtra("url");

        // Get references of all views
        response = (TextView) findViewById(R.id.result);
        instruction = (TextView) findViewById(R.id.textView4);
        wrong = (Button) findViewById(R.id.button);
        sendResult = (Button) findViewById(R.id.button3);
        correct = (Button) findViewById(R.id.button2);
        spinner = (Spinner) findViewById(R.id.spinner);
        response.setText(responseString);
        sendResult.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        instruction.setVisibility(View.INVISIBLE);

        // To make the Actionbar title color black
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name)  + "</font>"));

        // This is an ArrayList to select the correct output
        final List<String> digit = new ArrayList<String>();
        digit.add(0,"Choose Digit");
        digit.add("0");
        digit.add("1");
        digit.add("2");
        digit.add("3");
        digit.add("4");
        digit.add("5");
        digit.add("6");
        digit.add("7");
        digit.add("8");
        digit.add("9");

        // Make an adapter to add this array to the spinner view
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,digit);

        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);

        // Select first on by default
        spinner.setSelection(0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Digit")){}
                else
                {
                    selected = digit.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sendResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // By calling this function we will send results to the server to store correct output
                sendResult(selected,"wrong");
                 }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendResult("-1","correct");
                finish();
            }
        });

        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wrong.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.INVISIBLE);
                sendResult.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                instruction.setVisibility(View.VISIBLE);

            }
        });
    }

    public void sendResult(String result,String type) {
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("type",type);
            jsonBody.put("result", result);
            final String requestBody = jsonBody.toString();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/result",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                Toast.makeText(getApplicationContext(), "Thanks: Your Response Is Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "SERVER DOES NOT RESPOND", Toast.LENGTH_SHORT).show();
                    finish();
                    Log.e("VOLLEY", error.toString());
                }
            })
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");

                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String parsed;
                    try {
                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        parsed = new String(response.data);
                    }
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1.0f));
            queue.add(stringRequest);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}

