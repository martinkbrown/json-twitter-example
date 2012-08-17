package co.martinbrown.example.jsontwitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import co.martinbrown.example.httpgetparsing.R;

public class JsonTwitterExample extends Activity {

    EditText mEditMoviePlot;
    Button mButtonSubmit;
    WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mEditMoviePlot = (EditText) findViewById(R.id.editText1);
        mButtonSubmit = (Button) findViewById(R.id.buttonSubmit);
        mWebView = (WebView) findViewById(R.id.webView1);
    }

    public void getProfileImage(View v) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    executeHttpGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void loadUrlFromJson(String raw) throws Exception {

        JSONArray json = new JSONArray(raw);
        JSONObject jsono = json.getJSONObject(0);
        JSONObject user = jsono.getJSONObject("user");
        final String imageUrl = user.getString("profile_image_url");

        /*runOnUiThread(new Runnable() {

            @Override
            public void run() {*/
        mWebView.loadUrl(imageUrl);
        /*}
        });*/
    }

    public void executeHttpGet() throws Exception {
        BufferedReader in = null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://twitter.com/statuses/user_timeline/"+
                    mEditMoviePlot.getText().toString() +  ".json");
            HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

            switch(statusCode) {

                case 200:

                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    String NL = System.getProperty("line.separator");

                    while ((line = in.readLine()) != null) {
                        sb.append(line + NL);
                    }

                    in.close();

                    loadUrlFromJson(sb.toString());

                    break;

            }

        }
        finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}