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

import java.util.Calendar;
import java.util.HashMap;

public class WFragmentUserProfile extends Fragment {

    public String rUserName, rEnrollKey, rFullName, rBranch, rRoom, rContact, rUserImage;
    ImageView rUserImageView;
    Bitmap myBitmap;
    TextView fNameII, UserNameII, EnrollmentII, BranchII, NumberII, RoomII;
    Button LogOut;
    private AppData mSession;

    private WFragmentUserProfileInteractionListener mListener;

    public WFragmentUserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.wfragment_user_profile, container, false);

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

        mSession = new AppData(getActivity());

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

        LogOut = (Button) view.findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Press", "Button Pressed LOG OUT");
                mSession.setLogin(false);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
        if (context instanceof WFragmentUserProfileInteractionListener) {
            mListener = (WFragmentUserProfileInteractionListener) context;
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


    public interface WFragmentUserProfileInteractionListener {
        void onWFragmentUserProfileInteraction(Uri uri);
    }
}
