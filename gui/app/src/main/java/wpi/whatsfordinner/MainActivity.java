package wpi.whatsfordinner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> listData = new ArrayList<>();
    private IngredientAdapter iAdapter;
    private RestCalls restCalls = new RestCalls();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout clickableRelative;
    private FrameLayout blurLayout;
    private PopupWindow popUpWindow;
    private RecyclerView popupRecyclerView;

    /**
     * This initializes the main activity
     * In the future we may very well place all of this in a fragment, but for now, this is fine the way it is, happens when I am alone
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //make this not blacked out
        blurLayout = findViewById(R.id.main_activity_blur);
        blurLayout.getForeground().setAlpha(0);

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
                        //This is the case where we select home on the home screen, don't need to worry about doing anything
                        break;
                    case 1:
                        //This is the case where we select my diet -- if user has not logged in yet, we should redirect them to log in
                        //otherwise we send them directly to their fitness page
                        break;
                    case 2:
                        //This is the settings page -- we should allow users to filter search options here
                        break;
                    case 3:
                        //This is the about page -- don't think anyone will ever even click on this
                        break;
                    case 4:
                        //logout case
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putBoolean("logged_in", false);
                        edit.commit();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });

        //initialize so we have one card up
        ingredients.add("");

        clickableRelative = findViewById(R.id.clickable_relative);
        clickableRelative.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //we want to blur out the background -- do this first so there is no delay
                blurLayout.getForeground().setAlpha(220);

                //initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                //inflate the custom layout/view
                View customView = inflater.inflate(R.layout.main_popup, null);

                //set up the popup window
                popUpWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                //set the recyclerview
                popupRecyclerView = customView.findViewById(R.id.popup_recycler);
                RelativeLayout layout = customView.findViewById(R.id.main_r_relative_layout);
                popupRecyclerView.setLayoutManager(new LinearLayoutManager(layout.getContext(), LinearLayoutManager.VERTICAL, false));

                IngredientAdapter popUpAdapter = new IngredientAdapter(ingredients);
                popupRecyclerView.setAdapter(popUpAdapter);

                //need to check the SDK version
                if(Build.VERSION.SDK_INT>=21){
                    popUpWindow.setElevation(5.0f);
                }

                //this is the button we use to close the popup
                Button cancelButton = customView.findViewById(R.id.cancel_button);

                //this is the title of the popup view
                TextView textView = customView.findViewById(R.id.main_popup_text);

                //set the title name
                textView.setText("List your ingredients");

                //this is the plus image button
                ImageButton iButton = customView.findViewById(R.id.plus_sign);
                iButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredients.add("");
                        updateUI();
                    }
                });

                //this is the submit button
                Button sButton = customView.findViewById(R.id.go_button);
                sButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //convert the arraylist of ingredients to a simple array of strings that can be processed by the rest api
                        String[] ings = new String[ingredients.size()];
                        ings = ingredients.toArray(ings);

                        //reinitialize the list, in case user made a new search
                        listData.clear();
                        //get the results of the restcall response
                        JSONArray object = restCalls.getIngredients(ings);

                        //if the array of JSON objects is not null, lets add it to our list, we will modify this later on
                        if(object != null){
                            try {
                                for(int i = 0; i < object.length(); i++){
                                    listData.add(object.getString(i));
                                }
                            } catch (JSONException e){
                                System.out.println(e.getMessage());
                            }
                        }

                        //print out this list, just to check
                        System.out.println(Arrays.toString(listData.toArray()));

                        //alright, we are going to go to the next activity to let the user select which recipe they
                        //want to view
                        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        intent.putStringArrayListExtra("recipeList", listData);
                        startActivity(intent);
                    }
                });

                //when we press the close button we should close the popup
                cancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        ingredients.clear();
                        ingredients.add("");
                        iAdapter = null;
                        blurLayout.getForeground().setAlpha(0);
                        popUpWindow.dismiss();
                    }
                });

                //show the equipment popup on the main relative view
                RelativeLayout mainLayout = findViewById(R.id.main_r);

                popUpWindow.setFocusable(true);
                popUpWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                updateUI();
            }
        });

        //disable strictmode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        if (iAdapter == null){
            iAdapter = new IngredientAdapter(ingredients);
            popupRecyclerView.setAdapter(iAdapter);
        } else {
            //otherwise just update the adapter's data and notify the change
            iAdapter.setAdapterIngredients(ingredients);
            iAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This gives us our own custom onTextChange Listener that we can use for each of the editTexts in the ingredients recyclerview
     */
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        /**
         * Updates which editText we point to
         * @param position
         */
        public void updatePosition(int position){
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3){

        }

        /**
         * Only one we care about, after user has finished making an edit to a given editText,
         * We can add the ingredient to the list of ingredients
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable){
            ingredients.set(position, editable.toString());
        }
    }

    /**
     * This is the holder object for the ingredient recyclerview
     */
    private class IngredientHolder extends RecyclerView.ViewHolder {

        //edit text that is found in the recycler view, where users can put ingredients in the box
        private EditText editText;
        //this gives the edit text a listener that will update our arraylist of ingredients when they finish editing a particular box
        private MyCustomEditTextListener myCustomEditTextListener;

        /**
         * Initializes the holder
         * @param inflater
         * @param parent
         * @param myCustomEditTextListener
         */
        public IngredientHolder(LayoutInflater inflater, ViewGroup parent, MyCustomEditTextListener myCustomEditTextListener) {
            super(inflater.inflate(R.layout.ingredient_card, parent, false));
            this.editText = itemView.findViewById(R.id.editText);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);
        }

        /**
         * Binds data to the holder
         * @param label
         */
        public void bind(String label){
            editText.setText(label);
        }
    }

    /**
     * Adapter class for the ingredients recyclerview
     */
    private class IngredientAdapter extends RecyclerView.Adapter<IngredientHolder> {
        //data source
        private ArrayList<String> adapterIngredients;

        /**
         * Constructor
         * @param ingredient
         */
        public IngredientAdapter(ArrayList<String> ingredient){
            this.adapterIngredients = ingredient;
        }

        /**
         * Makes the holder
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public IngredientHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
            return new IngredientHolder(layoutInflater, parent, new MyCustomEditTextListener());
        }

        /**
         * Binds data
         * @param holder
         * @param i
         */
        @Override
        public void onBindViewHolder(IngredientHolder holder, int i){
            String label = adapterIngredients.get(i);
            holder.myCustomEditTextListener.updatePosition(i);
            holder.bind(label);
        }

        /**
         * Get the amount of ingredients
         * @return
         */
        @Override
        public int getItemCount() {
            return adapterIngredients.size();
        }

        /**
         * Add a way to update the data source
         * @param ingredient
         */
        public void setAdapterIngredients(ArrayList<String> ingredient){
            this.adapterIngredients = ingredient;
        }
    }
}
