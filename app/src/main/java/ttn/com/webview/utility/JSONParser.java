package ttn.com.webview.utility;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This class downloads the JSON data from URL
 */
public class JSONParser extends AsyncTask<String, JSONObject, JSONObject>{

    GetJsonDataFromAsyncTask getJsonDataFromAsyncTask;
    String url;

    // Send data to MainActivity class, helps in parsing JSON data with the help of URL.
    public interface GetJsonDataFromAsyncTask {
        JSONObject doInBackground(String url);
        void doPostExecute(JSONObject jObj);
    }

    public JSONParser(GetJsonDataFromAsyncTask callBack, String jsonUrl) {
        getJsonDataFromAsyncTask = callBack;
        url = jsonUrl;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jObj = getJsonDataFromAsyncTask.doInBackground(url);
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject jObj) {
        super.onPostExecute(jObj);
        getJsonDataFromAsyncTask.doPostExecute(jObj);
    }
}