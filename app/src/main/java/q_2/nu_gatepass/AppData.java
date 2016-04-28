package q_2.nu_gatepass;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Pradumn K Mahanta on 10-03-2016.
 **/
public class AppData {
    public static final String ULRCheckLogin = "http://gatepass.esy.es/checklogin.php";

    public static final String ULRAddRequests = "http://gatepass.esy.es/addrequest.php";

    public static final String ULRGetRequests = "http://gatepass.esy.es/getrequests.php";

    public static final String ULRUpdateRequests = "http://gatepass.esy.es/updaterequests.php";

    public static final String ULRGetRequestsWARDEN = "http://gatepass.esy.es/wardengetrequests.php";

    public static final String ULRAddGCMID = "http://gatepass.esy.es/addgcmid.php";

    public static final String ULRGetImage = "http://gatepass.esy.es/getimage.php?user_name=";

    public static SharedPreferences LoginDetails;

    public static SharedPreferences LoggedInUser;

    public static SharedPreferences UserImages;

    public static SharedPreferences GCMRegisteration;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";


    private static final String PREF_NAME = "GatepassSystem";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static String TAG = AppData.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public AppData(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);

    }

}
