package q_2.nu_gatepass;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Pradumn K Mahanta on 05-04-2016.
 **/

public class GCMInstanceIDListenerService extends InstanceIDListenerService{

    private static final String TAG = "InstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}