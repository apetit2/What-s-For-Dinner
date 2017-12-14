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

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //if user was previously logged in, no need to log in again -- go directly to the main activity
        if (preferences.getBoolean("logged_in", false)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        name = findViewById(R.id.name_text);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);

        problemOne = findViewById(R.id.name_problem);
        problemTwo = findViewById(R.id.email_problem);
        problemThree = findViewById(R.id.password_problem);

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
            JSONObject result = restCalls.userSignUp(name.getText().toString(), email.getText().toString(), password.getText().toString());
            if(result != null){
                try{
                    String status = result.getString("status");
                    if (status.equals("success")){
                        return "success";
                    } else {
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
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("logged_in", true);
                editor.putString("display_name", name.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
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



