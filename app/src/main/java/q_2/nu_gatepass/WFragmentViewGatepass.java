package q_2.nu_gatepass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WFragmentViewGatepass extends Fragment {
    private OnWViewGatepassFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeContainer;
    private List<GatepassListViewItem> mItems;
    String rUserName;
    View view;
    GetRequests getRequests;

    public WFragmentViewGatepass() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWViewGatepassFragmentInteractionListener) {
            mListener = (OnWViewGatepassFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);
        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rUserName = AppData.LoggedInUser.getString("rUserName", "");

        Toast.makeText(getActivity(), "UserName : " + rUserName, Toast.LENGTH_SHORT).show();

        getRequests = new GetRequests();
        view = inflater.inflate(R.layout.sfragment_view_gatepass, container, false);
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

        mItems = new ArrayList<GatepassListViewItem>();

        Log.d("Username", rUserName);

        getRequests.execute(rUserName);

        // Set the adapter
        if (view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.findViewById(R.id.list).getContext();
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new WFragmentViewGatepassAdapter(mItems, mListener));
        }
        return view;
    }


    public interface OnWViewGatepassFragmentInteractionListener {
        void onWViewGatepassFragmentInteraction(Context mContext, GatepassListViewItem item);
    }

    class GetRequests extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching your data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("request_to", args[0]);
                params.put("request_status", "Pending");
                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRGetRequestsWARDEN, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject jsonOb) {
            String rStudentName, rUserName, rRequestStatus, rRequestTo,
                    rEnrollmentNo, rOutDate, rOutTime, rInDate, rInTime,
                    rRequestTime, rApprovedTime, rVisitPlace, rVisitType,
                    rContactNo, rGatepassNumber, rPurpose, rReason;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.setMessage("Done.");
                pDialog.dismiss();
            }

            try {

                JSONArray jsonAr = jsonOb.getJSONArray("gatepass_request");

                for (int i=0; i<jsonAr.length(); i++) {
                    JSONObject json = jsonAr.getJSONObject(i);
                    rGatepassNumber = json.getString("gatepass_number");
                    rStudentName = json.getString("student_name");
                    rUserName = json.getString("user_name");
                    rPurpose = json.getString("purpose");
                    rRequestStatus = json.getString("request_status");
                    rRequestTo = json.getString("request_to");
                    rEnrollmentNo = json.getString("enrollment_no");
                    rOutDate = json.getString("out_date");
                    rOutTime = json.getString("out_time");
                    rInDate = json.getString("in_date");
                    rInTime = json.getString("in_time");
                    rRequestTime = json.getString("request_time");
                    rApprovedTime = json.getString("approved_time");
                    rVisitPlace = json.getString("visit_place");
                    rVisitType = json.getString("visit_type");
                    rReason = json.getString("reason");
                    rContactNo = json.getString("contact_number");
                    mItems.add(new GatepassListViewItem(rGatepassNumber, rStudentName, rUserName, rRequestStatus, rRequestTo,
                            rEnrollmentNo, rOutDate, rOutTime, rInDate, rInTime, rVisitPlace, rVisitType, rContactNo, rPurpose, rReason,
                            rRequestTime, rApprovedTime));
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
                recyclerView.setAdapter(new WFragmentViewGatepassAdapter(mItems, mListener));
            }

        }
    }
}
