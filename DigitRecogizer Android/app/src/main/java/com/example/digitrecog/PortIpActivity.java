package com.example.digitrecog;
/**
 * @Author: Pushpendra Kumar
 *
 * In this activity we set the Api to run the application
 * And connect to a server
 * @note: Please enter api in the given forms
 * 1. Enter you ip if you have ip
 * 2. Enter you port with ip If you have
 * 3. Enter a http://your-address
 * 4. When you enter http address then do not enter you port number
 * 5. We have 3 Api calls
 *    a. api/train to train the model
 *    b. api/result to send the correct results for futher training
 *    c. api/predict to predict the results
 * All api's will give you string
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PortIpActivity extends AppCompatActivity {

    // Variables which are used in this activity
    private static int SPLASH_TIME_OUT = 2200;
    private Button setApi;
    private EditText addIp,addPort;
    private String api_ip,port,url;

    /**
     * This methos will called automatically when this activity starts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To hide the Notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // To set the content of layout to activity
        setContentView(R.layout.activity_port_ip);

        // Getting references from layout
        setApi = (Button)findViewById(R.id.setApi);
        addIp = (EditText) findViewById(R.id.addIp);
        addPort = (EditText) findViewById(R.id.addPort);

        // This will change the color of the text written in ActionBar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name)  + "</font>"));

        // This is a listner, called when we click on the SetApi button
        setApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will give you an alert in the form of toast message to set api first
                if ( ( addIp.getText().toString().trim().equals("")) )
                {
                    Toast.makeText(getApplicationContext(), "Please Must Enter IP/Api ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    api_ip=addIp.getText().toString();
                    port = addPort.getText().toString();

                    // this function will make your url of api according to need
                    makeUrl(api_ip,port);

                    Toast.makeText(getApplicationContext(), "Url setting to: "+url, Toast.LENGTH_LONG).show();

                    // This will delay your next activity launch to ensure you enter correct api
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PortIpActivity.this, MainActivity.class);

                            // With this line of code we are sending url to the next activity
                            intent.putExtra("url", url);
                            startActivity(intent);

//                            // Here we finishing the activity means we can't come back here by back button
//                            // we have start app again
//                            finish();
                        }
                    },SPLASH_TIME_OUT);
                }
            }
        });
    }

    /**
     * makeUrl method will make your url
     * @param api_ip
     * @param port
     * here first parameter is your ip or api with http
     * second one os you port number if required
     */
    public void makeUrl(String api_ip, String port)
    {
        String upToNCharacters = api_ip.substring(0, Math.min(api_ip.length(), 4));
        if (upToNCharacters.equals("http"))
        {
            url = api_ip;
        }
        else
        {
            if (port == null || port.isEmpty())
            {
                url = "http://"+api_ip;
            }
            else
            {
                url = "http://"+api_ip+":"+port;
            }
        }
    }
}