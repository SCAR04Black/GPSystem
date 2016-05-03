package q_2.nu_gatepass;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class SFragmentApplyGatepass extends Fragment{

    private SFragmentApplyGatepassInteractionListener mListener;
    AutoCompleteTextView purpose, destination;
    String out_time_sel, in_time_sel, in_date_sel, out_date_sel;
    public Button out_time, in_time, request, in_date, out_date;
    public String rUserName, rEnrollKey, rFullName, rBranch, rRoom, rContact, rDestination, rPurpose;

    ImageView rUserImageView;


    public SFragmentApplyGatepass() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sfragment_apply_gatepass, container, false);

        purpose = (AutoCompleteTextView) view.findViewById(R.id.purpose);
        destination = (AutoCompleteTextView) view.findViewById(R.id.destination);

        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rUserName = AppData.LoggedInUser.getString("rUserName", "");
        rEnrollKey = AppData.LoggedInUser.getString("rEnrollKey", "");
        rFullName = AppData.LoggedInUser.getString("rFullName", "");
        rBranch = AppData.LoggedInUser.getString("rBranch", "");
        rRoom = AppData.LoggedInUser.getString("rRoom", "");
        rContact = AppData.LoggedInUser.getString("rContact", "");


        rUserImageView = (ImageView) view.findViewById(R.id.rUserImageView);

        if(Objects.equals(AppData.LoggedInUser.getString("rPic", ""), "")){
            GetImage getImage = new GetImage(getActivity(), rUserImageView);
            getImage.execute(rUserName);
        }else{
            Bitmap myBitmap;
            String rUserImage = AppData.LoggedInUser.getString("rPic", "");
            byte[] imgBytesData = Base64.decode(rUserImage, Base64.DEFAULT);
            myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            rUserImageView.setImageBitmap(myBitmap);
        }


        Log.d("Bundle : ", "Restored.");
        out_time = (Button) view.findViewById(R.id.out_time);
        out_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        out_time.setText(selectedHour + ":" + selectedMinute);
                        out_time_sel = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select OUT TIME: ");
                mTimePicker.show();
            }
        });

        in_time = (Button) view.findViewById(R.id.in_time);
        in_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        in_time.setText(selectedHour + ":" + selectedMinute);
                        in_time_sel = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select IN TIME: ");
                mTimePicker.show();
            }
        });

        in_date = (Button) view.findViewById(R.id.in_date);
        in_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth = selectedmonth + 1;
                        in_date.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        in_date_sel = "" + selectedday + "/" + selectedmonth + "/" + selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select IN Date:");
                mDatePicker.show();
            }
        });

        out_date = (Button) view.findViewById(R.id.out_date);
        out_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth = selectedmonth + 1;
                        out_date.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        out_date_sel = "" + selectedday + "/" + selectedmonth + "/" + selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select OUT Date:");
                mDatePicker.show();
            }
        });

        request = (Button) view.findViewById(R.id.request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rPurpose = purpose.getText().toString();
                rDestination = destination.getText().toString();
                AddRequest addRequest = new AddRequest(getActivity());
                addRequest.execute();
            }
        });
        return view;
    }


    public void getTime(Context ctx, final Button button){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                button.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SFragmentApplyGatepassInteractionListener) {
            mListener = (SFragmentApplyGatepassInteractionListener) context;
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



    public interface SFragmentApplyGatepassInteractionListener {
        // TODO: Update argument type and name
        void onSFragmentApplyGatepassInteraction(Uri uri);
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
                params.put("purpose", rPurpose);
                params.put("request_status", "Pending");
                params.put("request_to", "dummy.warden");
                params.put("enrollment_no", rEnrollKey);
                params.put("out_date", out_date_sel);
                params.put("out_time", out_time_sel);
                params.put("in_date", in_date_sel);
                params.put("in_time", in_time_sel);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                int seconds = mcurrentTime.get(Calendar.SECOND);
                params.put("request_time", String.valueOf(hour) + " : " + String.valueOf(minute) + " : " + String.valueOf(seconds));
                params.put("approved_time", "Not Required");
                params.put("visit_place", rDestination);
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
}
