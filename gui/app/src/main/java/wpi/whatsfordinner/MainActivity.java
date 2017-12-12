package wpi.whatsfordinner;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> listData = new ArrayList<>();
    private RecyclerView ingredientRecyclerView;
    private LinearLayout linLayout;
    private IngredientAdapter iAdapter;
    private ImageButton iButton;
    private Button submitButton;

    /**
     * This initializes the main activity
     * In the future we may very well place all of this in a fragment, but for now, this is fine the way it is, happens when I am alone
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //disable strictmode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //initialize so we have one card up
        ingredients.add("");

        //get the recyclerview from the user interface
        ingredientRecyclerView = findViewById(R.id.recycler_view);

        //get the linear layout that holds the recycler view, need this to set the linear layout manager of the recyclerview
        linLayout = findViewById(R.id.lin_layout);

        //set the layout manager of the recyclerview so that it actually works
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(linLayout.getContext(), LinearLayoutManager.VERTICAL, false));

        //image button, is used to add more ingredients that we then submit a search for
        iButton = findViewById(R.id.imageButton);

        /**
         * Gets a click listener for the image button, essentially adds a new item to the array of ingredients which places another box into the recyclerview
         */
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ingredients.add("");
                System.out.println(Arrays.toString(ingredients.toArray()));
                updateUI();
            }
        });

        //this might be replaced later on, might make this happen automatically, but for now, this is fine
        submitButton = findViewById(R.id.submitButton);


        /**
         * adds a click listener to the submit button, this essentially calls the rest api to get potential recipes for the user
         */
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //convert the arraylist of ingredients to a simple array of strings that can be processed by the rest api
                String[] ings = new String[ingredients.size()];
                ings = ingredients.toArray(ings);

                //reinitialize the list, in case user made a new search
                listData.clear();
                //get the results of the restcall response
                JSONArray object = RestCalls.getIngredients(ings);

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

        //updates the ingredients recycler view
        updateUI();
    }

    /**
     * Adds menu to this activity, although not sure if we want this
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Updates the ingredients recyclerview
     */
    private void updateUI(){
        //if we have yet to set our adapter, than we should now
        if (iAdapter == null){
            iAdapter = new IngredientAdapter(ingredients);
            ingredientRecyclerView.setAdapter(iAdapter);
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
