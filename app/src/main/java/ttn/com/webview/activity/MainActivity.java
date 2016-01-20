package ttn.com.webview.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ttn.com.webview.R;
import ttn.com.webview.adapter.ItemAdapter;
import ttn.com.webview.model.ParseJSONUrlData;
import ttn.com.webview.utility.Constants;
import ttn.com.webview.utility.JSONParser;
import ttn.com.webview.utility.JSONParser.GetJsonDataFromAsyncTask;

/**
 * @description This class loads the JSON data, parse, and attach the adapter to display webview names.
 */
public class MainActivity extends Activity implements GetJsonDataFromAsyncTask {

    ParseJSONUrlData parseJSONUrlData;
    JSONParser jSONParser;
    JSONObject jsonObject;
    ListView mList_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList_webview = (ListView) findViewById(R.id.list_webview);

        try {
            jSONParser = new JSONParser(this, Constants.mJsonUrl);
            jSONParser.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create WebViewProject directory inside files for storing cache files separate.
        createStorageParentDirectory();
    }

    private void createStorageParentDirectory() {
        File file = new File(getFilesDir() + File.separator + Constants.folderName);
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Load JSON data from URL
    @Override
    public JSONObject doInBackground(String url) {
        JSONObject jObject = parseJsonData(url);
        return jObject;
    }

    // Parse JSON data
    @Override
    public void doPostExecute(JSONObject jObj) {
        jsonObject = jObj;
        ArrayList<ParseJSONUrlData> list = new ArrayList<ParseJSONUrlData>();
        try {
            JSONArray jsonArray = jsonObject.names();

            for (int i = 0; i < jsonObject.length(); i++) {
                parseJSONUrlData = new ParseJSONUrlData();

                if (jsonObject.getJSONObject(jsonArray.getString(i)).has("pageTitle")) {
                    parseJSONUrlData.setUrl(jsonObject.getJSONObject(jsonArray.getString(i)).getString("url"));
                    parseJSONUrlData.setPageTitle(jsonObject.getJSONObject(jsonArray.getString(i)).getString("pageTitle"));
                    parseJSONUrlData.setCache(jsonObject.getJSONObject(jsonArray.getString(i)).getBoolean("cache"));
                    parseJSONUrlData.setFilePath(jsonObject.getJSONObject(jsonArray.getString(i)).getString("filePath"));
                    parseJSONUrlData.setNamespace(jsonObject.getJSONObject(jsonArray.getString(i)).getString("namespace"));

                    list.add(parseJSONUrlData);
                }
            }
            replaceURLSpecification(list);
            displayListWithWebViewNames(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Attach adapter and send directory, url, cache and filename to Webview page.
    private void displayListWithWebViewNames(final ArrayList<ParseJSONUrlData> list) {
        // Create the adapter to convert the array to views
        ItemAdapter adapter = new ItemAdapter(this, list);
        // Attach the adapter to a ListView
        mList_webview.setAdapter(adapter);

        mList_webview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename;
                if (list.get(position).getFilePath().contains("/")) {
                    filename = list.get(position).getFilePath().substring(list.get(position).getFilePath().indexOf("/") + 1, list.get(position).getFilePath().length()) + ".html";
                } else {
                    filename = list.get(position).getFilePath() + ".html";
                }
                Intent intent = new Intent(MainActivity.this, WebViewContentActivity.class);
                intent.putExtra("directory", list.get(position).getNamespace());
                intent.putExtra("filename", filename);
                intent.putExtra("url", list.get(position).getUrl());
                intent.putExtra("cache", list.get(position).isCache());
                startActivity(intent);
            }
        });
    }

    // Replace url specifications
    private void replaceURLSpecification(ArrayList<ParseJSONUrlData> list) {
        for (int i = 0; i < list.size(); i++) {
            String url = list.get(i).getUrl();
            if (url.contains(Constants.userIdKey)) {
                url = url.replace(Constants.userIdKey, Constants.userIdKeyValue);
            }

            if (url.contains(Constants.appSecretKey)) {
                url = url.replace(Constants.appSecretKey, Constants.appSecretKeyValue);
            }

            if (url.contains(Constants.currencyCodeKey)) {
                url = url.replace(Constants.currencyCodeKey, Constants.currencyCodeKeyValue);
            }

            if (url.contains(Constants.offerIdKey)) {
                url = url.replace(Constants.offerIdKey, Constants.offerIdKeyValue);
            }

            if (url.contains(Constants.selectedVoucherKey)) {
                url = url.replace(Constants.selectedVoucherKey, Constants.selectedVoucherKeyValue);
            }

            list.get(i).setUrl(url);
        }
    }

    JSONObject parseJsonData(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        try {
            // defaultHttpClient
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
            Log.d("json", json + "");
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }
}
