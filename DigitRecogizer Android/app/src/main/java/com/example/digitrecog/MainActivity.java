package com.example.digitrecog;
/**
 * @Author: Pushpendra Kumar
 * MainActivity is the Main activity where draw some digits and
 * this will gives us result on the next activity
 *
 * Here we can train out model also on new dataset
 */

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyLog;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.Menu;


public class MainActivity extends AppCompatActivity {

    // Variables to use in this class
    private Button btnClear, btnPredict;
    private AlertDialog.Builder dialogbuilder;
    private Button back;
    private LinearLayout canvasLL;
    private View view;
    private AlertDialog dialog;
    private DigitDraw digitDraw;
    public TextView textview;
    private ProgressDialog progressDialog,progressDialog2;
    public String url;
    public String encodedimage;
    private Bitmap bitmap;

    //Creating Separate Directory for saving Generated Images and also getting path
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Signature/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".jpeg";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // With below code we can get the data passed from last activity
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // This is again to set title to black color actionBar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>"));

        // Getting references of views from layout
        canvasLL = (LinearLayout) findViewById(R.id.canvasLL);
        digitDraw = new DigitDraw(getApplicationContext(), null);
        digitDraw.setBackgroundColor(Color.WHITE);
        canvasLL.addView(digitDraw, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnClear = (Button) findViewById(R.id.btnclear);
        btnPredict = (Button) findViewById(R.id.btnpredict);
        view = canvasLL;

        // To reset/clear the canvas
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digitDraw.clear();
            }
        });

        // To hit the api which will predict the digit and give it back to us
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // To make the processing dialog until we get response from server
                view.setDrawingCacheEnabled(true);
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Prediction In Process");
                progressDialog.setMessage("Please Wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                // function call where actually api hit to the server
                predict(view, StoredPath);

            }
        });
    }

    /**
     * In this function we are make an menu which is in the actioBar
     * @param menu take
     * @return boolean value true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * this fucntion will take care what should be happen when we hit the menu item
     * @param item which item is selected
     * @return boolean true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trainmodel:
                // User chose the "Settings" item, show the app settings UI...
                progressDialog2 = new ProgressDialog(MainActivity.this);
                progressDialog2.setTitle("Training In Process");
                progressDialog2.setMessage("Please Wait... It may took longer");
                progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog2.setCancelable(false);
                progressDialog2.show();

                // Here call the model where Api hit and training will starts
                trainModel();
                return true;


            case R.id.about:

                // This will show you the About dialog whcih will tells you about the application
                createNewDialog();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /**
     *
     * @param v view where we have all the componets like canvas buttons
     * @param StoredPath this is a path where our image will stored
     *                   in local memory if you want.
     *
     */
    public void predict(View v , String StoredPath) {
        // to get data from the view and a bitmap
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        try {
            /**
             * If we enable this code then this image will also save in you phone memory
             */
            //FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
            //v.draw(canvas);
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
            //mFileOutStream.flush();
            //mFileOutStream.close();

            v.draw(canvas);

            // First we create an object of byte arrays stream to store image in the form of bytes
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

            // here we are compressing bitmap into a bytearray stream
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

            // Here we are converting the image to bytearray
            byte[] byteofimages = byteArrayOutputStream.toByteArray();

            // Now we are encoding this image to Base64 encoding
            encodedimage = Base64.encodeToString(byteofimages, Base64.DEFAULT);

            // with this function we will send this image to the api to predict the results
            sendImage();

        } catch (Exception e) {
            // with this exception handling we create folder to save images if there is not any folder
            e.printStackTrace();
            File file = new File(DIRECTORY);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    // this function will send a request to the server to train the model
    public void trainModel() {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/train",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                progressDialog2.dismiss();
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog2.dismiss();
                    Toast.makeText(getApplicationContext(), "SERVER DOES NOT RESPOND", Toast.LENGTH_SHORT).show();
                    Log.e("VOLLEY", error.toString());
                }
            });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 1, 1.0f));
        queue.add(stringRequest);
        }


        // To send the image to api for prediction
    public void sendImage() {
        try{
            // first we make a json object or out input to the api
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("image", encodedimage);

        // The convert jsonbody object to a string
        final String requestBody = jsonBody.toString();

        // this is like a common code to make a request to a server with volley library
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/predict",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            // When we get response from server then progress dialog will closed and
                            // send our result to next activity to show the results
                            progressDialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("response", response);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Dismiss the progress bar and show the error message in toast
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "SERVER DOES NOT RESPOND", Toast.LENGTH_SHORT).show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3500, 1, 1.0f));
        queue.add(stringRequest);
    }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // This function will called when we click on about button and shows up a dialog
    public void createNewDialog(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.about_popup,null);
        back = (Button) popupView.findViewById(R.id.back);
        textview = (TextView) popupView.findViewById(R.id.predictedText);
        dialogbuilder.setView(popupView);
        dialog = dialogbuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}