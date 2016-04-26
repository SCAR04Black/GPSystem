package q_2.nu_gatepass;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class SFragmentApplyGatepass extends Fragment implements AdapterView.OnItemSelectedListener {

    private SFragmentApplyGatepassFragmentInteractionListener mListener;
    View view;
    TextView lHeading;
    Spinner typeSelector;
    AutoCompleteTextView visitPurpose;
    FrameLayout vPurpose, sTime, sDate, rSubmit;
    Button iTime, oTime,  iDate, oDate, rSubmitR;


    public SFragmentApplyGatepass() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sfragment_apply_gatepass_temp, container, false);

        /*lHeading = (TextView) view.findViewById(R.id.lHeading);
        typeSelector = (Spinner) view.findViewById(R.id.typeSelector);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gatepass_type, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSelector.setAdapter(spinnerAdapter);

        vPurpose = (FrameLayout) view.findViewById(R.id.vPurpose);
        visitPurpose = (AutoCompleteTextView) view.findViewById(R.id.visitPurpose);
        vPurpose.setVisibility(View.INVISIBLE);

        sTime = (FrameLayout) view.findViewById(R.id.sTime);
        iTime = (Button) view.findViewById(R.id.iTime);
        oTime = (Button) view.findViewById(R.id.oTime);
        sTime.setVisibility(View.INVISIBLE);

        sDate = (FrameLayout) view.findViewById(R.id.sDate);
        iDate = (Button) view.findViewById(R.id.iDate);
        oDate = (Button) view.findViewById(R.id.oDate);
        oDate.setVisibility(View.INVISIBLE);

        rSubmit = (FrameLayout) view.findViewById(R.id.rSubmit);
        rSubmitR = (Button) view.findViewById(R.id.rSubmitR);
        rSubmit.setVisibility(View.INVISIBLE);
        */
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SFragmentApplyGatepassFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                /*v
                Purpose.setVisibility(View.VISIBLE);
                sTime.setVisibility(View.VISIBLE);
                rSubmit.setVisibility(View.VISIBLE);
                */
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface SFragmentApplyGatepassFragmentInteractionListener {
        void onSFragmentApplyGatepassFragmentInteraction(Uri uri);
    }
}
