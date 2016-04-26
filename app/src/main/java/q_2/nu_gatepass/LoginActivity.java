package q_2.nu_gatepass;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A login screen that offers login via Username/Password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public String rPass, rUserType, rUserName, rEnrollKey, rFullName, rBranch, rJoinDate, rHostel, rRoom, rContact, rEmail, rAddress;
    public String mPassword;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    public TextView desc,textViewName;
    public String fontpath = "fonts/ROCK.TTF";
    private Intent user_page_intent;
    private AppData mSession;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LoginActivity";
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        textViewName = (TextView) findViewById(R.id.textViewName);
        desc = (TextView) findViewById(R.id.desc);

        AppData.GCMRegisteration = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = AppData.GCMRegisteration.getBoolean(AppData.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(getApplicationContext(), "Data sent to server.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data not sent to server.", Toast.LENGTH_LONG).show();
                }
            }};

        registerReceiver();

        mSession = new AppData(getApplicationContext());
        if (mSession.isLoggedIn()) {
            Intent intent = new Intent(this, Container.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState != null) {
            AppData.LoginDetails = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
            String restoredEmail = AppData.LoginDetails.getString("rUserName", "");
            String restoredPassword = AppData.LoginDetails.getString("rPass", "");
            mEmailView.setText(restoredEmail);
            mPasswordView.setText(restoredPassword);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        user_page_intent = new Intent(this, Container.class);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (isNetworkAvailable()) {
                mAuthTask = new UserLoginTask();
                mAuthTask.execute(email);
                mPassword = password;
            } else {
                ShowDialog(LoginActivity.this, "Unable to Connect", "Please Check your Internet connection.");
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowDialog(Context context, String Title, String Message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onBackPressed() {
        //Container.super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        System.exit(0);
    }



    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(AppData.REGISTRATION_COMPLETE));
            Log.d("Registeration", "Reciever Registered");
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d("Registeration", "Play Services Available");
        return true;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class UserLoginTask extends AsyncTask<String, String, JSONObject>{

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog, pDialogII;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args)  {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_name", args[0]);
                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRCheckLogin, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                try {
                    rUserType = json.getString("u_type");
                    rPass = json.getString("u_password");
                    rUserName = json.getString("u_name");
                    rEnrollKey = json.getString("u_enroll");
                    rFullName = json.getString("u_fname");
                    rBranch = json.getString("u_course");
                    rJoinDate = json.getString("u_joining");
                    rHostel = json.getString("u_hostel");
                    rRoom = json.getString("u_room");
                    rContact = json.getString("u_contact");
                    rEmail = json.getString("u_email");
                    rAddress = json.getString("u_address");
                    //r_pic = json.getString("u_pic");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Objects.equals(rPass, mPassword)) {
                AppData.LoginDetails = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = AppData.LoginDetails.edit();
                editor.putString("ACC_NAME", rUserName);
                editor.putString("ACC_PASS", rPass);
                editor.apply();

                AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor2 = AppData.LoginDetails.edit();
                editor2.putString("rUserType", rUserType);
                editor2.putString("rPass", rPass);
                editor2.putString("rUserName", rUserName);
                editor2.putString("rEnrollKey", rEnrollKey);
                editor2.putString("rFullName", rFullName);
                editor2.putString("rBranch", rBranch);
                editor2.putString("rJoinDate", rJoinDate);
                editor2.putString("rHostel", rHostel);
                editor2.putString("rRoom", rRoom);
                editor2.putString("rContact", rContact);
                editor2.putString("rEmail", rEmail);
                editor2.putString("rAddress", rAddress);
                editor2.apply();;

                Toast.makeText(LoginActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                user_page_intent.putExtra("ACC_TYPE", rUserType);
                user_page_intent.putExtra("ACC_NAME", rUserName);

                pDialogII = new ProgressDialog(LoginActivity.this);
                pDialogII.setMessage("Registering for notifications...");
                pDialogII.setIndeterminate(false);
                pDialogII.setCancelable(false);
                pDialogII.show();

                if (checkPlayServices()) {
                    Intent intent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
                    startService(intent);
                    Log.d("Registeration", "Registeration Intent Started");
                }

                if (pDialogII != null && pDialogII.isShowing()) {
                    pDialogII.dismiss();
                }

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                mSession.setLogin(true);
                startActivity(user_page_intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
