package com.bluecats.services;

import android.os.AsyncTask;

import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Base64;
//import org.apache.commons.codec.binary.Base64; //make sure to import this into the build path

/**
 * Created by dgurun00c on 11/28/2016.
 */

public class SmsAsyncTask extends AsyncTask <String, Void, Integer>

{

    @Override
     protected Integer doInBackground(String... params) {

            try {
                String phoneNumber = "4846887576";
                String appKey = "6f0356c3-c29c-4eb5-a7c5-659f1d2d64df";
                String appSecret = "csKdjI2Dl0SzZGZhc8Msfg==";
                String message = "Hello, world!";
                URL url = new URL("https://messagingapi.sinch.com/v1/sms/" + phoneNumber);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                String userCredentials = "application\\" + appKey + ":" + appSecret;
                byte[] encoded = Base64.encode(userCredentials.getBytes(),Base64.NO_WRAP);
                //byte[] encoded = Base64.encodeBase64(userCredentials.getBytes());
                String basicAuth = "Basic " + new String(encoded);
                connection.setRequestProperty("Authorization", basicAuth);
                String postData = "{\"Message\":\"" + message + "\"}";
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                StringBuilder response = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ( (line = br.readLine()) != null)
                    response.append(line);
                br.close();
                os.close();
                System.out.println(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        return 0;

    }

    }

