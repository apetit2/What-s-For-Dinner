package wpi.whatsfordinner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by apand on 12/10/2017.
 */

public class RecipeActivity extends AppCompatActivity {
    private ArrayList<String> recipes = new ArrayList<>();
    private RecipeAdapter rAdapter;
    private RecyclerView recipeRecyclerView;
    private LinearLayout linLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        //sets up the menu drawer
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

        //click listener for menu items on left slide out drawer
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        //we want to go home
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        //This is the case where we select my diet -- if user has not logged in yet, we should redirect them to log in
                        Intent intent4 = new Intent(getApplicationContext(), DietActivity.class);
                        startActivity(intent4);
                        break;
                    case 2:
                        //This is the settings page -- we should allow users to filter search options here
                        Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        //This is the about page -- don't think anyone will ever even click on this
                        Intent intent2 = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent2);
                        break;
                    case 4:
                        //logout case
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putBoolean("logged_in", false);
                        edit.commit();

                        Intent intent5 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent5);
                        break;
                    default:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });

        //get the list of recipes found from querying the API in the last activity
        recipes = getIntent().getStringArrayListExtra("recipeList");

        //set up the recycler view to display each recipe
        recipeRecyclerView = findViewById(R.id.recipe_recycler_view);
        linLayout = findViewById(R.id.recipe_lin_layout);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(linLayout.getContext(), LinearLayoutManager.VERTICAL, false));

        updateUI();
    }

    //menu setup functions
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
     * Updates the recipes recyclerview
     */
    private void updateUI(){
        //if we have yet to set our adapter, than we should now
        if (rAdapter == null){
            rAdapter = new RecipeAdapter(recipes);
            recipeRecyclerView.setAdapter(rAdapter);
        } else {
            //otherwise just update the adapter's data and notify the change
            rAdapter.setAdapterRecipes(recipes);
            rAdapter.notifyDataSetChanged();
        }
    }

    private class RecipeHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private int id;
        private String imagePath;

        /**
         * Initializes the holder
         * @param inflater
         * @param parent
         */
        public RecipeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recipes_card, parent, false));
            this.textView = itemView.findViewById(R.id.textView);
        }

        /**
         * Binds data to the holder
         * @param label
         */
        public void bind(String label) throws JSONException {
            JSONObject jsonObject = new JSONObject(label);
            String recipeName = jsonObject.getString("title");
            id = jsonObject.getInt("id");
            imagePath = jsonObject.getString("image");

            textView.setText(recipeName);
        }
    }

    /**
     * Adapter class for the ingredients recyclerview
     */
    private class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder> {
        //data source
        private ArrayList<String> adapterRecipes;

        /**
         * Constructor
         * @param recipes
         */
        public RecipeAdapter(ArrayList<String> recipes){
            this.adapterRecipes = recipes;
        }

        /**
         * Makes the holder
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
            return new RecipeHolder(layoutInflater, parent);
        }

        /**
         * Binds data
         * @param holder
         * @param i
         */
        @Override
        public void onBindViewHolder(final RecipeHolder holder, int i){
            String label = adapterRecipes.get(i);
            try {
                holder.bind(label);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    System.out.println(holder.id);
                    System.out.println(holder.imagePath);

                    Intent intent = new Intent(getApplicationContext(), RecipeDisplay.class);
                    intent.putExtra("id", holder.id);
                    intent.putExtra("imagePath", holder.imagePath);
                    intent.putExtra("title", holder.textView.getText());
                    startActivity(intent);
                }
            });
        }

        /**
         * Get the amount of recipes
         * @return
         */
        @Override
        public int getItemCount() {
            return adapterRecipes.size();
        }

        /**
         * Add a way to update the data source
         * @param recipes
         */
        public void setAdapterRecipes(ArrayList<String> recipes){
            this.adapterRecipes = recipes;
        }
    }
}
