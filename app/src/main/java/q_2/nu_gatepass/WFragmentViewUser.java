package q_2.nu_gatepass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class WFragmentViewUser extends Fragment {

    private WFragmentViewUserInteractionListener mListener;
    View view;
    ArrayList<UserListViewItem> mItems;
    SwipeRefreshLayout swipeContainer;


    public WFragmentViewUser() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wfragment_view_user, container, false);

        GetUsers getUsers = new GetUsers(getActivity());
        getUsers.execute("STUDENT");

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Placeholder
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeContainer.setRefreshing(false);
                            }
                        });
                    }
                }, 2000);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mItems = new ArrayList<UserListViewItem>();

        return  view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (WFragmentViewUserInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface WFragmentViewUserInteractionListener {
        void onWFragmentViewUserInteractionListener(Context mContext, UserListViewItem uItem);
    }

    class GetUsers extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;
        Context gContext;
        GetUsers(Context mContext) {
            this.gContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching user data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_type", args[0]);
                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRGetUsers, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject jsonOb) {
            String rStudentName, rUserName, rPic, rStatus,
                    rEnrollmentNo, rContactNo, rBranch, rBatch;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.setMessage("Done.");
                pDialog.dismiss();
            }

            try {

                JSONArray jsonAr = jsonOb.getJSONArray("user_data");

                for (int i=0; i<jsonAr.length(); i++) {
                    JSONObject json = jsonAr.getJSONObject(i);
                    rStudentName = json.getString("user_fullname");
                    rUserName = json.getString("user_name");
                    rEnrollmentNo = json.getString("user_enrollment");
                    rContactNo = json.getString("user_contact");
                    rBranch = json.getString("user_course");
                    rBatch = json.getString("user_joining");
                    rPic = json.getString("user_pic");
                    rStatus = json.getString("user_status_");

                    AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(gContext);
                    SharedPreferences.Editor editor = AppData.LoggedInUser.edit();
                    editor.putString(rUserName, rPic);
                    editor.apply();

                    mItems.add(new UserListViewItem(rUserName, rStudentName, rBatch, rEnrollmentNo, rBranch, rContactNo, rPic,
                            rStatus));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (view.findViewById(R.id.list) instanceof RecyclerView) {
                Context context = view.findViewById(R.id.list).getContext();
                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new WFragmentViewUserAdapter(mItems, mListener));
            }

        }
    }

}
