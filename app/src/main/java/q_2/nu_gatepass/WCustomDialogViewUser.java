package q_2.nu_gatepass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Pradumn K Mahanta on 02-05-2016.
 **/

public class WCustomDialogViewUser extends Dialog implements android.view.View.OnClickListener{

    public Context mContext;
    public Button rWhite, rBlack;
    TextView rFullName, rBatch, rUserName, rEnrollmentNo, rContact, rStatus;
    UpdateStatus updateStatus;
    ImageView rUserImageView;
    public UserListViewItem mItem;

    public WCustomDialogViewUser (Context mContext, UserListViewItem mItem) {
        super(mContext, R.style.Theme_CustomDialog);
        this.mContext = mContext;
        this.mItem = mItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wcustom_dialog_viewuser);

        rUserImageView = (ImageView) findViewById(R.id.rUserImageView);

        Bitmap myBitmap;
        byte[] imgBytesData = Base64.decode(mItem.rPic, Base64.DEFAULT);
        myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        rUserImageView.setImageBitmap(myBitmap);


        rBlack = (Button) findViewById(R.id.rBlack);
        rWhite = (Button) findViewById(R.id.rWhite);
        rBlack.setOnClickListener(this);
        rWhite.setOnClickListener(this);

        rFullName = (TextView) findViewById(R.id.rFullName);
        rFullName.setText(mItem.rFullName);

        updateStatus = new UpdateStatus(mContext);

        rBatch = (TextView) findViewById(R.id.rBatch);
        rBatch.setText(mItem.rBatch);

        rUserName = (TextView) findViewById(R.id.rUserName);
        rUserName.setText(mItem.rUserName);

        rEnrollmentNo = (TextView) findViewById(R.id.rEnrollmentNo);
        rEnrollmentNo.setText(mItem.rEnrollKey);

        rContact = (TextView) findViewById(R.id.rContact);
        rContact.setText(mItem.rContact);

        rStatus = (TextView) findViewById(R.id.rStatus);
        String sStatus = mItem.rStatus;
        if(Integer.valueOf(sStatus) == 1){
            rStatus.setText("Active");
            rStatus.setTextColor(Color.BLUE);
        }else{
            rStatus.setText("Inactive");
            rStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rWhite:
                updateStatus.execute("1", mItem.rUserName);
                dismiss();
                break;
            case R.id.rBlack:
                updateStatus.execute("0", mItem.rUserName);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


    class UpdateStatus extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        Context ctxt;

        UpdateStatus(Context ctx){
            ctxt = ctx;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Updating Status...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_status_", args[0]);
                params.put("user_name", args[1]);

                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRUpdateStatus, "GET", params);
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
        }
    }
}
