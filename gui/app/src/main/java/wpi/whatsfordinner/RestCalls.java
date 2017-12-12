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

    public static String url;

    /**
     * Gets a list of 20 recipes based upon the ingredients the user gave
     * @param ingredients {String[]} -- a list of all ingredients the user is searching recipes for
     * @return
     */
    public static JSONArray getIngredients(String[] ingredients) {

        //Make a post request to my server to determine 20 recipes that best match the user's search
        try {
            //set up a post request to the url: url + "/service/api/ingredients"
            HttpPost post = new HttpPost(RestCalls.url + "/service/api/ingredients");
            //set up our httpclient -- this will be used to make the post request
            HttpClient httpclient = HttpClientBuilder.create().build();

            //request body set up, should have list of ingredients that we send out
            //this will in turn return a response of recipes
            List<NameValuePair> nameValuePairs = new ArrayList<>(); //add size to arraylist

            for(String ingredient: ingredients){
                nameValuePairs.add(new BasicNameValuePair("ingredients[]", ingredient));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //get our response
            HttpResponse response = httpclient.execute(post);

            //check to make sure the status was good, if not, we should return null
            int status = response.getStatusLine().getStatusCode();
            if(status == 200){
                //if we are good, we can proceed and return a json array of the received recipes
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

        //we should probably do some error checking somewhere along the way
        return null;
    }

    /**
     * If we do not have recipe in database, we should request our api to give us information on it
     * @param id {int} -- the id of the recipe in the api database
     * @return
     */
    public static String getRecipe(int id) {
        try {
            //set up a post request for the url - url + '/service/api/recipe'
            HttpPost post = new HttpPost(RestCalls.url + "/service/api/recipe");
            //http client to make the request
            HttpClient httpClient = HttpClientBuilder.create().build();

            //set up the response body for the request, need an id for request body
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("id", "" + id));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //get response
            HttpResponse response = httpClient.execute(post);

            //check to make sure the server is sending back a good response
            int status = response.getStatusLine().getStatusCode();
            if(status == 200){
                //if it is, we can send out the response of the server
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                System.out.println(data);

                return data;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        //probably should do some more error checking, pretty sure as it stands if no internet connection this will crash the app
        return null;
    }

    /**
     * Check if recipe exists in our database, if it does, we can return that recipe information from there, so we don't over abuse the api
     * @param title
     * @return
     */
    public static String checkDatabaseFirst(String title){
        try {
            //set up the post request with url + /service/data/item
            HttpPost post = new HttpPost(RestCalls.url + "/service/data/item");
            HttpClient httpClient = HttpClientBuilder.create().build();

            //set up request parameters
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("title", title));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //get back the response from the server
            HttpResponse response = httpClient.execute(post);

            //if the response is good from the server, we can return data to the user
            int status = response.getStatusLine().getStatusCode();
            if(status == 200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                return data;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        //need to do some more error checking
        return null;
    }
}
