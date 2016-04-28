package q_2.nu_gatepass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Pradumn K Mahanta on 12-04-2016.
 **/
public class WCustomDialog extends Dialog implements android.view.View.OnClickListener{

    public Context mContext;
    public Button rApprove, rReject;
    AutoCompleteTextView rReason;
    UpdateRequest updateRequest;
    ImageView rUserImageView;
    public GatepassListViewItem mItem;
    TextView sName, sBatch, inTime, inDate, outTime, visitPurpose, visitPlace, outDate;
    String acc_type, acc_name, acc_username;

    public WCustomDialog(Context mContext, GatepassListViewItem mItem) {
        super(mContext, R.style.Theme_CustomDialog);
        this.mContext = mContext;
        this.mItem = mItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wcustom_dialog);

        rUserImageView = (ImageView) findViewById(R.id.rUserImageView);
        GetImage getImage = new GetImage(mContext, rUserImageView);
        getImage.execute(mItem.gp_UserName);


        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(mContext);
        acc_type = AppData.LoggedInUser.getString("rUserType", "");
        acc_name = AppData.LoggedInUser.getString("rFullName", "");

        acc_username = AppData.LoggedInUser.getString("rUserName", "");

        rApprove = (Button) findViewById(R.id.rApprove);
        rReject = (Button) findViewById(R.id.rReject);
        rApprove.setOnClickListener(this);
        rReject.setOnClickListener(this);

        sName = (TextView) findViewById(R.id.sName);
        sName.setText(mItem.gp_StudentName);

        updateRequest = new UpdateRequest(mContext);

        inTime = (TextView) findViewById(R.id.inTime);
        inTime.setText(mItem.gp_InTime);

        inDate = (TextView) findViewById(R.id.inDate);
        inDate.setText(mItem.gp_InDate);

        outTime = (TextView) findViewById(R.id.outTime);
        outTime.setText(mItem.gp_OutTime);

        outDate = (TextView) findViewById(R.id.outDate);
        outDate.setText(mItem.gp_OutDate);

        visitPlace = (TextView) findViewById(R.id.visitPlace);
        visitPlace.setText(mItem.gp_VisitPlace);

        visitPurpose = (TextView) findViewById(R.id.visitPurpose);
        visitPurpose.setText(mItem.gp_Purpose);

        sBatch = (TextView) findViewById(R.id.sBatch);
        sBatch.setText(mItem.gp_EnrollmentNo);

        rReason = (AutoCompleteTextView) findViewById(R.id.rReason);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rApprove:
                    updateRequest.execute("Approved", rReason.getText().toString(), mItem.gp_GatepassNumber, mItem.gp_UserName);
                    dismiss();
                break;
            case R.id.rReject:
                    updateRequest.execute("Rejected", rReason.getText().toString(), mItem.gp_GatepassNumber, mItem.gp_UserName);
                    dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


    class UpdateRequest extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        Context ctxt;

        UpdateRequest(Context ctx){
            ctxt = ctx;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Updating Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("request_status", args[0]);
                params.put("reason", args[1]);
                params.put("gatepass_number", args[2]);
                params.put("approved_by", acc_username);
                params.put("user_name", args[3]);

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                int seconds = mcurrentTime.get(Calendar.SECOND);
                params.put("approved_time", String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(seconds) );

                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRUpdateRequests, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Log.d("Object", json.toString());

            if(json != null) {
                try {
                    if (json.getBoolean("result0")) {
                        Toast.makeText(ctxt, "Request updated.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ctxt, "Update Failed.", Toast.LENGTH_LONG).show();
                    }
                    if (json.getBoolean("result1")) {
                        Toast.makeText(ctxt, "ID Retrieved.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ctxt, "Retrieve Failed.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                }
            }
        }
    }
}