package ttn.com.webview.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import ttn.com.webview.R;
import ttn.com.webview.utility.Constants;

/**
 * @description This class shows the web view content and filter for load data from cache or online.
 */
public class WebViewContentActivity extends Activity {

    private WebView mWebView = null;
    private String url= null;
    private String filename= null;
    private String directory = null;
    private String path= null;
    private String pathTillDirectory= null;
    boolean isCache = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_content);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        filename = getIntent().getStringExtra("filename");
        url = getIntent().getStringExtra("url");
        directory = getIntent().getStringExtra("directory");
        isCache = getIntent().getBooleanExtra("cache", false);

        // path till directory.
        pathTillDirectory = getFilesDir() + File.separator + directory;
        // path till file
        path = getFilesDir() + File.separator + directory + File.separator + filename;
        File file = new File(path);

        if (file.exists() && file.canRead()) {
            // if cache file exist and readable
            loadCacheData(file);
        } else {
            // if no cache available
            showWebContentOnline();
        }
    }

    // Manage progress dialog and load url
    private void showWebContentOnline() {
        final ProgressDialog progressDialog = new ProgressDialog(WebViewContentActivity.this);
        mWebView.loadUrl(url);
        if (Constants.isNetworkConnected(WebViewContentActivity.this)) {
            progressDialog.show();
            mWebView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });

            if (isCache)
            {
                new downloadContentOfPage().execute();
            }
        } else {
            Toast.makeText(WebViewContentActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    // Open cache file from storage
    private void loadCacheData(File file) {
        mWebView.loadUrl("file:///" + file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view_content, menu);
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

    // Create directory first then file and save content inside.
    class downloadContentOfPage extends AsyncTask<String, Void, String> {
        File file;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            File fileDirectory = new File(pathTillDirectory);
            fileDirectory.mkdirs();

            file = new File(path);
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return saveOfflineDataInStorage();
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                OutputStream fo = new FileOutputStream(file);
                fo.write(res.getBytes());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Save data in file for offline usage.
        private String saveOfflineDataInStorage() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            //give your url of html page that you want to download first time.
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpGet, localContext);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()
                        )
                );
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    result += line + "\n";
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }
    }
}