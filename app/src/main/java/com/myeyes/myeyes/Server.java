package com.myeyes.myeyes;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.opencv.core.CvException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

public class Server extends AsyncTask<String,Void,Void> {


    private String server = "http://192.168.0.20:8000";
    JSONObject postData;
    private String route = "";



    public Server(Map<String, String> postData) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            // This is getting the url from the string we passed in
            URL url = new URL(this.server+"/"+params[0] );

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");


            // OPTIONAL - Sets an authorization header
            urlConnection.setRequestProperty("Authorization", "someAuthString");




            // Send the post body
            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }



            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);

                // From here you can convert the string to JSON with whatever JSON parser you like to use

                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method

            } else {
                System.out.println("Error p Página no cargada");
            }

            urlConnection.disconnect();

        } catch (Exception e) {
            System.out.println("Error p "+e.getMessage());
        }
        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



}
