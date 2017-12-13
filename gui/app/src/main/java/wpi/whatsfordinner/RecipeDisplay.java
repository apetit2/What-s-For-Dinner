package wpi.whatsfordinner;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by apand on 12/10/2017.
 */

public class RecipeDisplay extends AppCompatActivity {
    private ImageView recipeImage;
    private TextView recipeText;
    private TextView timeText;
    private Button ingredientButton;
    private Button equipmentButton;
    private RecyclerView recipeStepRecyclerView;
    private RelativeLayout rLayout;
    private StepAdapter sAdapter;
    private String title;
    private int id;
    private String imagePath;
    private ArrayList<String> recipeSteps = new ArrayList<>();
    private ArrayList<String> equipment = new ArrayList<>();
    private ArrayList<String> ingredients = new ArrayList<>();
    private String time;
    private PopupWindow popUpWindow;
    private FrameLayout frameLayout;
    private RestCalls restCalls = new RestCalls();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.recipe_display);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView){

            }

            @Override
            public void onDrawerOpened(View drawerView){

            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        //make this not blacked out
        frameLayout = findViewById(R.id.main_menu);
        frameLayout.getForeground().setAlpha(0);

        //disable strictmode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //get the title of the recipe
        title = getIntent().getStringExtra("title");
        //get the id of the recipe, in case we need to look up from the rest api
        id = getIntent().getIntExtra("id", 0);
        //get the image path of the recipe so we can display an image of what it looks like
        imagePath = getIntent().getStringExtra("imagePath");

        //just get some of the views from the activity
        recipeImage = findViewById(R.id.recipeImage);
        recipeText = findViewById(R.id.recipeText);

        //set the title of the activity to be the recipe name
        recipeText.setText(title);

        //This only works, if and only if, the client has connection to the internet
        //The simulator appears to have problems connecting to any server except my localhost
        //look up the image to put into the image view
        try {
            URL url = new URL(imagePath);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            recipeImage.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //check to see if we already put this recipe in our database
        String check = restCalls.checkDatabaseFirst(title);
        System.out.println(check);

        //initializes a jsonarray, this will hold information on our recipe, received from the server
        JSONArray jsonArray = null;

        //Format our check in the proper format so that we can determine if the recipe is already in our recipe
        String checkString = null;
        try {
            JSONObject o = new JSONObject(check);
            checkString = o.getString("time");
            System.out.println(checkString);
        } catch (JSONException e) {
            //do nothing here, we should be getting exceptions if the database has not yet added the stuff yet
        }

        //if we hadn't already inserted that recipe, we will make a request to the rest api,
        //and then store it in our database
        if(checkString == null || checkString.equals("null")){
            System.out.println("We do not have the data available to us, need to search it");
            String data = restCalls.getRecipe(id);
            try {
                JSONObject json = new JSONObject(data);
                jsonArray = json.getJSONArray("instructions");
                time = json.getString("time");
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            //otherwise we can just look it up in our firebase database via call to server
            System.out.println("We already had the data available for free in the database");
            try {
                JSONObject json = new JSONObject(check);
                jsonArray = new JSONArray(json.getString("instructions"));
                time = json.getString("time");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //jsonArray should never be null, but just to be sure
        if(jsonArray != null){
            try {
                //add each json object from the array to a list of recipe steps
                parseJSON(jsonArray);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }

        //deleting duplicates
        final Set<String> ingredientsSet = new HashSet<>();
        ingredientsSet.addAll(ingredients);
        ingredients.clear();
        ingredients.addAll(ingredientsSet);

        Set<String> equipmentSet = new HashSet<>();
        equipmentSet.addAll(equipment);
        equipment.clear();
        equipment.addAll(equipmentSet);

        //printing out, to determine that we have gathered right stuff
        System.out.println(Arrays.toString(recipeSteps.toArray()));
        System.out.println(Arrays.toString(ingredients.toArray()));
        System.out.println(Arrays.toString(equipment.toArray()));
        System.out.println(time);

        //set the time duration
        timeText = findViewById(R.id.time_duration);
        timeText.setText(time);

        //set up the recyclerview
        recipeStepRecyclerView = findViewById(R.id.step_recyclerview);
        rLayout = findViewById(R.id.r_layout);
        recipeStepRecyclerView.setLayoutManager(new LinearLayoutManager(rLayout.getContext(), LinearLayoutManager.VERTICAL, false));

        updateUI();

        //we want to show the user a popup to show the equipment needed to make the recipe
        equipmentButton = findViewById(R.id.equipment_button);
        equipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we want to blur out the background -- do this first so there is no delay
                frameLayout.getForeground().setAlpha(220);

                //initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                //inflate the custom layout/view
                View customView = inflater.inflate(R.layout.equipment_popup, null);

                //set up the popup window
                popUpWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                //need to check the SDK version
                if(Build.VERSION.SDK_INT>=21){
                    popUpWindow.setElevation(5.0f);
                }

                //this is the button we use to close the popup
                Button closeButton = customView.findViewById(R.id.equipment_dismiss);
                //this is the title of the popup view
                TextView textView = customView.findViewById(R.id.equipment_text);

                //set the title name
                textView.setText("Equipment");

                //set the recyclerview
                RecyclerView popupRecyclerView = customView.findViewById(R.id.popup_recycler);
                RelativeLayout layout = customView.findViewById(R.id.popup_relative);
                popupRecyclerView.setLayoutManager(new LinearLayoutManager(layout.getContext(), LinearLayoutManager.VERTICAL, false));

                StepAdapter popUpAdapter = new StepAdapter(equipment);
                popupRecyclerView.setAdapter(popUpAdapter);

                //when we press the close button we should close the popup
                closeButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        frameLayout.getForeground().setAlpha(0);
                        popUpWindow.dismiss();
                    }
                });

                //show the equipment popup on the main relative view
                RelativeLayout mainLayout = findViewById(R.id.main_relative_layout);
                popUpWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

            }
        });

        //we want to show the user a popup to show the ingredients needed to make the recipe
        ingredientButton = findViewById(R.id.what_ingredients);

        ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we want to blur out the background -- do this first so there is no delay
                frameLayout.getForeground().setAlpha(220);

                //initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                //inflate the custom layout/view
                View customView = inflater.inflate(R.layout.equipment_popup, null);

                //set up the popup window
                popUpWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                //need to check the SDK version
                if(Build.VERSION.SDK_INT>=21){
                    popUpWindow.setElevation(5.0f);
                }

                //this is the button we use to close the popup
                Button closeButton = customView.findViewById(R.id.equipment_dismiss);
                //this is the title of the popup view
                TextView textView = customView.findViewById(R.id.equipment_text);

                //set the recyclerview
                RecyclerView popupRecyclerView = customView.findViewById(R.id.popup_recycler);
                RelativeLayout layout = customView.findViewById(R.id.popup_relative);
                popupRecyclerView.setLayoutManager(new LinearLayoutManager(layout.getContext(), LinearLayoutManager.VERTICAL, false));

                StepAdapter popUpAdapter = new StepAdapter(ingredients);
                popupRecyclerView.setAdapter(popUpAdapter);

                //set the title name
                textView.setText("Ingredients");

                //when we press the close button we should close the popup
                closeButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        frameLayout.getForeground().setAlpha(0);
                        popUpWindow.dismiss();
                    }
                });

                //show the equipment popup on the main relative view
                RelativeLayout mainLayout = findViewById(R.id.main_relative_layout);
                popUpWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Updates the ingredients recyclerview
     */
    private void updateUI(){
        //if we have yet to set our adapter, than we should now
        if (sAdapter == null){
            sAdapter = new StepAdapter(recipeSteps);
            recipeStepRecyclerView.setAdapter(sAdapter);
        } else {
            //otherwise just update the adapter's data and notify the change
            sAdapter.setSteps(recipeSteps);
            sAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This parses our json object to get the recipe steps, the equipment needed for the recipe, the ingredients, and the time
     * @param array
     * @throws JSONException
     */
    public void parseJSON(JSONArray array) throws JSONException {
        //get the recipe steps from the json object
        System.out.println(array.length());
        for(int i = 0; i < array.length(); i++) {
            //add each step to the list of recipe steps
            JSONObject o = array.getJSONObject(i);
            System.out.println(o.getString("step"));
            recipeSteps.add(o.getString("step"));

            //get ingredients for each step
            JSONArray ings = o.getJSONArray("ingredients");
            //add ingredients to the list of ingredients
            for (int j = 0; j < ings.length(); j++) {
               JSONObject iObject = ings.getJSONObject(j);
               ingredients.add(iObject.getString("name"));
            }

            //get equipment for each step
            JSONArray equip = o.getJSONArray("equipment");
            //add equipment to the array of equipment
            for (int j = 0; j < equip.length(); j++){
                JSONObject eObject = equip.getJSONObject(j);
                equipment.add(eObject.getString("name"));
            }
        }
    }

    private class StepHolder extends RecyclerView.ViewHolder {

        //the recycled text view that displays the steps
        private TextView stepView;

        /**
         * A view holder for the recipe step recyclerview
         * @param inflater
         * @param parent
         */
        public StepHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.step_card, parent, false));
            this.stepView = itemView.findViewById(R.id.step_text_view);
        }

        /**
         * How we want to bind the data to the aforementioned textview
         * @param step
         */
        public void bind(String step){
            stepView.setText(step);
        }
    }

    private class StepAdapter extends RecyclerView.Adapter<StepHolder> {
        //datasource
        ArrayList<String> steps;

        /**
         * Constructor
         * @param steps
         */
        public StepAdapter(ArrayList<String> steps) {this.steps = steps; }

        /**
         * Set the view holder for the step recyclerview
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public StepHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new StepHolder(layoutInflater, parent);
        }

        /**
         * Bind data to the view holder
         * @param holder
         * @param i
         */
        @Override
        public void onBindViewHolder(StepHolder holder, int i){
            String step = steps.get(i);
            holder.bind(step);
        }

        /**
         * Get the amount of data in the data source
         * @return
         */
        @Override
        public int getItemCount() {return steps.size(); }

        /**
         * Set the data source
         * @param steps
         */
        public void setSteps(ArrayList<String> steps) {this.steps = steps; }
    }
}
