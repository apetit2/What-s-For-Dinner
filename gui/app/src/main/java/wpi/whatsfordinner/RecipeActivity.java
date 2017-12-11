package wpi.whatsfordinner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        recipes = getIntent().getStringArrayListExtra("recipeList");

        recipeRecyclerView = findViewById(R.id.recipe_recycler_view);
        linLayout = findViewById(R.id.recipe_lin_layout);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(linLayout.getContext(), LinearLayoutManager.VERTICAL, false));

        updateUI();
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
