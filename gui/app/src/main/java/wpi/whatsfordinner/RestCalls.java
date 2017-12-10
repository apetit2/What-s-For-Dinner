package wpi.whatsfordinner;

/**
 * Created by apand on 12/8/2017.
 */

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class RestCalls {

    public static String url = "";

    public JSONArray getIngredients(String[] ingredients) {
        try {
            HttpPost post = new HttpPost(this.url + "/service/api/ingredients");
            HttpClient httpclient = HttpClientBuilder.create().build();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //add size to arraylist

            for(String ingredient: ingredients){
                nameValuePairs.add(new BasicNameValuePair("ingredients[]", ingredient));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(post);

            int status = response.getStatusLine().getStatusCode();

            if(status == 200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                System.out.println(data);

                JSONArray jsonObject = new JSONArray(data);

                return jsonObject;
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
