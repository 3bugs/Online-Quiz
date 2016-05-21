package com.promlert.onlinequiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.promlert.onlinequiz.model.ResponseStatus;
import com.promlert.onlinequiz.model.User;
import com.promlert.onlinequiz.net.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_REGISTER = 1;
    protected static final String USERNAME_INTENT_KEY = "username_intent";

    protected static final String COL_ID = "_id";
    protected static final String COL_NAME = "name";
    protected static final String COL_LASTNAME = "lastname";
    protected static final String COL_EMAIL = "email";
    protected static final String COL_PASSWORD = "password";
    protected static final String COL_PHONE = "phone";
    protected static final String COL_DATE_ADDED = "date_added";

    private static final String LOGGED_USER_ID_PREF_KEY = "logged_user_id";
    private static final String LOGGED_USER_NAME_PREF_KEY = "logged_user_name";
    private static final String LOGGED_USER_LASTNAME_PREF_KEY = "logged_user_lastname";
    private static final String LOGGED_USER_EMAIL_PREF_KEY = "logged_user_email";
    private static final String LOGGED_USER_PHONE_PREF_KEY = "logged_user_phone";

    private static final int NOT_LOGGED_USER_ID = 0;

    private static enum ScreenType {
        MAIN, LOGIN, SHOW_WAIT, HIDE_WAIT
    }

    private View mMainScreen, mLoginScreen, mWaitScreen;
    private Button mLoginButton, mLogoutButton;
    private TextView mRegisterTextView;
    private EditText mUsernameEditText, mPasswordEditText;

    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.loggedInUser != null) {
            Intent intent = new Intent(LoginActivity.this, QuizListActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        mMainScreen = findViewById(R.id.main_screen);
        mLoginScreen = findViewById(R.id.login_screen);
        mWaitScreen = findViewById(R.id.wait_screen);

        mLoginButton = (Button) findViewById(R.id.login_button);
        //mLogoutButton = (Button) findViewById(R.id.logout_button);
        mRegisterTextView = (TextView) findViewById(R.id.register_text);

        mUsernameEditText = (EditText) findViewById(R.id.username);
        mPasswordEditText = (EditText) findViewById(R.id.password);

        mLoginButton.setOnClickListener(this);
        //mLogoutButton.setOnClickListener(this);
        mRegisterTextView.setOnClickListener(this);

        SpannableString text = new SpannableString("New user? Register here.");
        text.setSpan(new UnderlineSpan(), text.toString().indexOf("Register"), text.length(), 0);
        mRegisterTextView.setText(text);

        updateUI();
    }

    private void updateUI() {
        //final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //int currentUserId = sharedPref.getInt(LOGGED_USER_ID_PREF_KEY, NOT_LOGGED_USER_ID);

        if (User.loggedInUser == null) {
            setScreen(ScreenType.LOGIN);
        } else {
/*
            String name = sharedPref.getString(LOGGED_USER_NAME_PREF_KEY, "");
            String lastName = sharedPref.getString(LOGGED_USER_LASTNAME_PREF_KEY, "");
            String text = String.format("Welcome %s %s", name, lastName);
            ((TextView) findViewById(R.id.welcome_text)).setText(text);
*/

            setScreen(ScreenType.MAIN);
        }
    }

    private void setScreen(ScreenType type) {
        if (type == ScreenType.MAIN) {
            mMainScreen.setVisibility(View.VISIBLE);
            mLoginScreen.setVisibility(View.GONE);
        } else if (type == ScreenType.LOGIN) {
            mMainScreen.setVisibility(View.GONE);
            mLoginScreen.setVisibility(View.VISIBLE);
        } else if (type == ScreenType.SHOW_WAIT) {
            mWaitScreen.setVisibility(View.VISIBLE);
        } else if (type == ScreenType.HIDE_WAIT) {
            mWaitScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // ปุ่ม Log in
        if (viewId == R.id.login_button) {
            if (validateLoginForm()) {
                setScreen(ScreenType.SHOW_WAIT);

                String username = mUsernameEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                login(username, password);
            }
        }
        // ปุ่ม Log out
/*
        else if (viewId == R.id.logout_button) {
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            final SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putInt(LOGGED_USER_ID_PREF_KEY, NOT_LOGGED_USER_ID);
                            prefEditor.apply();

                            updateUI();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
*/
        // ข้อความ Register here
        else if (viewId == R.id.register_text) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REGISTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra(USERNAME_INTENT_KEY);
                mUsernameEditText.setText(username);
            }
        }
    }

    private boolean validateLoginForm() {
        boolean validForm = true;

        if ("".equals(mUsernameEditText.getText().toString().trim())) {
            mUsernameEditText.setError("Enter username");
            validForm = false;
        }
        if ("".equals(mPasswordEditText.getText().toString().trim())) {
            mPasswordEditText.setError("Enter password");
            validForm = false;
        }

        return validForm;
    }

    private void login(String username, String password) {
        WebServices.login(username, password, new WebServices.LoginCallback() {
            @Override
            public void onFailure(IOException e) {
                setScreen(ScreenType.HIDE_WAIT);
                showModalOkDialog("Error", "Unable to connect to server.");
            }

            @Override
            public void onResponse(ResponseStatus responseStatus, User user) {
                setScreen(ScreenType.HIDE_WAIT);

                if (user != null) {
                    // ล็อกอินสำเร็จ
                    User.loggedInUser = user;
                    //updateUI();

                    Intent intent = new Intent(LoginActivity.this, QuizListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // ล็อกอินไม่สำเร็จ
                    showModalOkDialog("Login Failed", "Invalid username or password.");
                }
            }
        });
    }

    private void authenticateUser(String username, String password) throws IOException {
        String url = Uri.parse("http://promlert.com/faceid/select_by_email_password.php")
                .buildUpon()
                .appendQueryParameter(COL_EMAIL, username)
                .appendQueryParameter(COL_PASSWORD, password)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setScreen(ScreenType.HIDE_WAIT);
                        showModalOkDialog("Error", "Unable to connect to server.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setScreen(ScreenType.HIDE_WAIT);
                    }
                });

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    int success = json.getInt("success");

                    if (success == 1) {
                        int loginSuccess = json.getInt("login_success");

                        if (loginSuccess == 1) {
                            JSONObject user = json.getJSONArray("users").getJSONObject(0);
                            int id = Integer.valueOf(user.getString(COL_ID));
                            String name = user.getString(COL_NAME);
                            String lastname = user.getString(COL_LASTNAME);
                            String email = user.getString(COL_EMAIL);
                            String phone = user.getString(COL_PHONE);

                            final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            final SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putInt(LOGGED_USER_ID_PREF_KEY, id);
                            prefEditor.putString(LOGGED_USER_NAME_PREF_KEY, name);
                            prefEditor.putString(LOGGED_USER_LASTNAME_PREF_KEY, lastname);
                            prefEditor.putString(LOGGED_USER_EMAIL_PREF_KEY, email);
                            prefEditor.putString(LOGGED_USER_PHONE_PREF_KEY, phone);
                            prefEditor.apply();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            });
                        } else if (loginSuccess == 0) {
                            // login ไม่สำเร็จ
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showModalOkDialog("Login Failed", "Invalid username or password.");
                                }
                            });
                        }
                    } else if (success == 0) {
                        // แจ้ง error ด้วย dialog
                        final String message = json.getString("message");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showModalOkDialog("Error", message);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showModalOkDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
