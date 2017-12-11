package wpi.whatsfordinner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by apand on 12/10/2017.
 */

public class RecipeDisplay extends AppCompatActivity {
    private ImageView recipeImage;
    private TextView recipeText;
    private String title;
    private int id;
    private String imagePath;

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.recipe_display);

        //disable strictmode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        title = getIntent().getStringExtra("title");
        id = getIntent().getIntExtra("id", 0);
        imagePath = getIntent().getStringExtra("imagePath");

        recipeImage = findViewById(R.id.recipeImage);
        recipeText = findViewById(R.id.recipeText);

        recipeText.setText(title);

        try {
            URL url = new URL(imagePath);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            recipeImage.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
