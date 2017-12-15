package wpi.whatsfordinner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by apand on 12/14/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    private FrameLayout blurLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private EditText amountOfRecipes;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle onSavedInstance){
        super.onCreate(onSavedInstance);
        setContentView(R.layout.settings_activity);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //make this not blacked out
        blurLayout = findViewById(R.id.main_activity_blur);
        blurLayout.getForeground().setAlpha(0);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //set up for drawerlayout for menu items
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // this sets up a drawerlayout for the menu
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView){

            }

            @Override
            public void onDrawerOpened(View drawerView){

            }
        };

        //add event listener to menu button at the top so that the drawer opens when pressed
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        //This is the case where we select home on the home screen, don't need to worry about doing anything
                        Intent intent4 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent4);
                        break;
                    case 1:
                        //This is the case where we select my diet -- if user has not logged in yet, we should redirect them to log in
                        //otherwise we send them directly to their fitness page
                        Intent intent3 = new Intent(getApplicationContext(), DietActivity.class);
                        startActivity(intent3);
                        break;
                    case 2:
                        //This is the settings page -- we should allow users to filter search options here
                        //but we are already here so doesn't matter
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

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });

        amountOfRecipes = findViewById(R.id.num_recipes);
        int amount = preferences.getInt("default_num_recipes", 20);

        System.out.println(amount);

        amountOfRecipes.setText("" + amount);

        amountOfRecipes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = preferences.edit();
                try {
                    System.out.println(s.toString());
                    editor.putInt("default_num_recipes", Integer.parseInt(s.toString()));
                    editor.commit();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    //more methods for slide out menu

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
}
