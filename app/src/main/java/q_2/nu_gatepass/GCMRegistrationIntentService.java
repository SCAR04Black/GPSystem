package q_2.nu_gatepass;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Pradumn K Mahanta on 05-04-2016.
 **/

public class GCMRegistrationIntentService extends IntentService{

    private static final String TAG = "RegisterationIntentService";

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppData.GCMRegisteration = PreferenceManager.getDefaultSharedPreferences(this);
        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(this);
        String rUserName = AppData.LoggedInUser.getString("rUserName", "");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("424029306193", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.d("ID", token);

            sendRegistrationToServer(token, rUserName);

            AppData.GCMRegisteration.edit().putBoolean(AppData.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.d("ID","Failed to retrieve");
            AppData.GCMRegisteration.edit().putBoolean(AppData.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(AppData.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void sendRegistrationToServer(String token, String rUserName) {
        AddGCMToken addGCMToken = new AddGCMToken();
        addGCMToken.execute(token, rUserName);
        Intent idSent = new Intent(AppData.SENT_TOKEN_TO_SERVER);
        LocalBroadcastManager.getInstance(this).sendBroadcast(idSent);
    }

    class AddGCMToken extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_gcmid", args[0]);
                params.put("user_name", args[1]);
                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRAddGCMID, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("result")) {
                    Toast.makeText(getApplicationContext(), "ID Added Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Request Failed.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
            }
        }
    }
}