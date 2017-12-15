package wpi.whatsfordinner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apand on 12/13/2017.
 */

public class LoginActivity extends Activity {
    EditText name;
    EditText email;
    EditText password;
    Button createAccount;
    TextView alreadyMember;
    RestCalls restCalls = new RestCalls();
    SharedPreferences preferences;
    TextView problemOne;
    TextView problemTwo;
    TextView problemThree;
    private FrameLayout blurLayout;
    private PopupWindow popUpWindow;
    private boolean shouldDisplayPopup = false;
    private String popUpMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //make this not blacked out
        blurLayout = findViewById(R.id.main_activity_blur);
        blurLayout.getForeground().setAlpha(0);

        //get the user preferences
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //if user was previously logged in, no need to log in again -- go directly to the main activity
        if (preferences.getBoolean("logged_in", false)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        //load in views for updates
        name = findViewById(R.id.name_text);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);

        problemOne = findViewById(R.id.name_problem);
        problemTwo = findViewById(R.id.email_problem);
        problemThree = findViewById(R.id.password_problem);

        //add a click listener to the create account button
        //that adds a user if able, and then logs them in
        createAccount = findViewById(R.id.account_button);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //only if the user has set all values
                if(!name.getText().toString().matches("") && !email.getText().toString().matches("") && !password.getText().toString().matches("")){
                    //make a call to the database to update entry
                    new SignupTask().execute("");
                } else {
                    //otherwise we should instruct the user to fill in the rest of the values
                    System.out.println("user forgot to fill in a field");
                    if(name.getText().toString().matches("")){
                        problemOne.setVisibility(View.VISIBLE);
                    }else {
                        problemOne.setVisibility(View.INVISIBLE);
                    }
                    if(email.getText().toString().matches("")){
                        problemTwo.setVisibility(View.VISIBLE);
                    }else {
                        problemTwo.setVisibility(View.INVISIBLE);
                    }
                    if(password.getText().toString().matches("")){
                        problemThree.setVisibility(View.VISIBLE);
                    }else {
                        problemThree.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        //if the user selects this text view, we should redirect them to the login page
        alreadyMember = findViewById(R.id.member_login);
        alreadyMember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MemberLogin.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Need an async task, not sure why it causes a problem here, but it does -- put the network request on separate thread
     */
    public class SignupTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... t) {
            //perform a sign up request
            JSONObject result = restCalls.userSignUp(name.getText().toString(), email.getText().toString(), password.getText().toString());
            if(result != null){
                try{
                    //check to see if the user was added
                    String status = result.getString("status");
                    if (status.equals("success")){
                        //if the user was added update the uid to shared preferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("uid", result.getString("uid"));
                        editor.commit();
                        //return success
                        return "success";
                    } else {
                        //else figure out what the problem was and return it
                        return result.getString("error");
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (result.equals("success")){
                //if the user was signed in, we should update the display_name, email, and logged_in values and start the main activity
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("logged_in", true);
                editor.putString("display_name", name.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.putInt("default_num_recipes", 20);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                //else alert the user of what the problem was with signing them up for the service
                System.out.println(result);

                shouldDisplayPopup = true;
                popUpMessage = result;
            }
        }

        @Override
        protected void onPreExecute(){

        }
    }
}



