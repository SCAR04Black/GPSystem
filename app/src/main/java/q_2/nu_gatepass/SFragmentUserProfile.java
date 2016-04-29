package q_2.nu_gatepass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;


public class SFragmentUserProfile extends Fragment {

    public String rUserName, rEnrollKey, rFullName, rBranch, rRoom, rContact, rUserImage;
    ImageView rUserImageView;
    Bitmap myBitmap;
    TextView fNameII, UserNameII, EnrollmentII, BranchII, NumberII, RoomII;
    Button LogOut, OCGP;
    AppData mSession;

    private SFragmentUserProfileInteractionListener mListener;

    public SFragmentUserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.sfragment_user_profile, container, false);

        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rUserName = AppData.LoggedInUser.getString("rUserName", "");
        rEnrollKey = AppData.LoggedInUser.getString("rEnrollKey", "");
        rFullName = AppData.LoggedInUser.getString("rFullName", "");
        rBranch = AppData.LoggedInUser.getString("rBranch", "");
        rRoom = AppData.LoggedInUser.getString("rRoom", "");
        rContact = AppData.LoggedInUser.getString("rContact", "");
        rUserImage = AppData.LoggedInUser.getString("rPic", "");
        Log.d("Bundle : ", "Restored.");

        rUserImageView = (ImageView) view.findViewById(R.id.rUserImageView);
        byte[] imgBytesData = Base64.decode(rUserImage, Base64.DEFAULT);
        myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        rUserImageView.setImageBitmap(myBitmap);

        fNameII = (TextView) view.findViewById(R.id.fNameII);
        fNameII.setText(rFullName);
        UserNameII = (TextView) view.findViewById(R.id.UserNameII);
        UserNameII.setText(rUserName);
        EnrollmentII = (TextView) view.findViewById(R.id.EnrollmentII);
        EnrollmentII.setText(rEnrollKey);
        BranchII = (TextView) view.findViewById(R.id.BranchII);
        BranchII.setText(rBranch);
        NumberII = (TextView) view.findViewById(R.id.NumberII);
        NumberII.setText(rContact);
        RoomII = (TextView) view.findViewById(R.id.RoomII);
        RoomII.setText(rRoom);

        mSession = new AppData(getActivity());

        LogOut = (Button) view.findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Press", "Button Pressed LOG OUT");
                mSession.setLogin(false);
                clearApplicationData(getActivity());
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        OCGP = (Button) view.findViewById(R.id.OCGP);
        OCGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Press", "Button Pressed OCGP");
                AddRequest addRequest = new AddRequest(getActivity());
                addRequest.execute();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SFragmentUserProfileInteractionListener) {
            mListener = (SFragmentUserProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LocalGatepassInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                File f = new File(appDir, s);
                if(deleteDir(f))
                    Log.i("Data", String.format("**************** DELETED -> (%s) *******************", f.getAbsolutePath()));
            }
        }
    }
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public interface SFragmentUserProfileInteractionListener {
        void onSFragmentUserProfileInteraction(Uri uri);
    }

    class AddRequest extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        Context ctxt;

        AddRequest(Context ctx){
            ctxt = ctx;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Sending Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("student_name", rFullName);
                params.put("user_name", rUserName);
                params.put("purpose", "Fixed Gatepass");
                params.put("request_status", "Pending");
                params.put("request_to", "dummy.warden");
                params.put("enrollment_no", rEnrollKey);

                Calendar mDate = Calendar.getInstance();
                int mtodaysDate = mDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mDate.get(Calendar.MONTH);
                int mYear = mDate.get(Calendar.YEAR);
                params.put("out_date", String.valueOf(mtodaysDate + "/" + mMonth + "/" + mYear));
                params.put("out_time", "17:30");
                params.put("in_date", String.valueOf(mtodaysDate + "/" + mMonth + "/" + mYear));
                params.put("in_time", "21:30");

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                int seconds = mcurrentTime.get(Calendar.SECOND);
                params.put("request_time", String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(seconds));
                params.put("approved_time", "Not Required");
                params.put("visit_place", "Local Areas");
                params.put("visit_type", "Others");
                params.put("contact_number", rContact);

                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRAddRequests, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {

            pDialog.setMessage("Sending Request...");

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                if (json.getBoolean("result")) {
                    Toast.makeText(ctxt, "Request made.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctxt, "Request Failed.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = null;
        super.onSaveInstanceState(outState);
    }
}
