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

public class MemberLogin extends Activity {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView returnHome;
    private RestCalls restCalls = new RestCalls();
    SharedPreferences preferences;
    private FrameLayout blurLayout;
    private PopupWindow popUpWindow;
    private boolean shouldDisplayPopup = false;
    private String popUpMessage;

    @Override
    public void onCreate(Bundle onSavedInstance){
        super.onCreate(onSavedInstance);
        setContentView(R.layout.member_activity);

        //make this not blacked out
        blurLayout = findViewById(R.id.main_activity_blur);
        blurLayout.getForeground().setAlpha(0);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        name = findViewById(R.id.name_text);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);

        loginButton = findViewById(R.id.account_button);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //only if the user has set all values
                if(!name.getText().toString().matches("") && !email.getText().toString().matches("") && !password.getText().toString().matches("")){
                    new LoginTask().execute("");
                } else {
                    //otherwise we should instruct the user to fill in the rest of the values
                    System.out.println("user forgot to fill in a field");
                }
            }
        });

        returnHome = findViewById(R.id.member_login);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Need an asynctask -- not sure why it causes problems here and not in other places, but we should move over to new thread
     */
    public class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... t) {
            JSONObject result = restCalls.userLogin(name.getText().toString(), email.getText().toString(), password.getText().toString());
            try {
                String status = result.getString("status");
                if(status.equals("success")){
                    String uid = result.getString("uid");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("uid", uid);
                }
                return status;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("success")){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("logged_in", true);
                editor.putString("display_name", name.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                //invalid password or email or username, let user know that
                System.out.println("invalid password or email or username");

                shouldDisplayPopup = true;
                popUpMessage = result;
            }
        }

        @Override
        protected void onPreExecute(){

        }
    }
}
