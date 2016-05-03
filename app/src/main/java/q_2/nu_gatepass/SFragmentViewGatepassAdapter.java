package q_2.nu_gatepass;

/**
 * Created by Pradumn K Mahanta on 03-04-2016.
 **/

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Objects;

public class SFragmentViewGatepassAdapter extends RecyclerView.Adapter<SFragmentViewGatepassAdapter.ViewHolder> {

    private final List<GatepassListViewItem> mValues;
    private final SFragmentViewGatepass.OnSViewGatepassFragmentInteractionListener mListener;

    public SFragmentViewGatepassAdapter(List<GatepassListViewItem> items, SFragmentViewGatepass.OnSViewGatepassFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sgatepass_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.GP.setText(holder.mItem.gp_GatepassNumber);
        holder.setStatus.setText(holder.mItem.gp_RequestStatus);
        holder.inTime.setText(holder.mItem.gp_InTime);
        holder.outTime.setText(holder.mItem.gp_OutTime);

        Bitmap myBitmap;
        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(holder.studentIcon.getContext());
        String rUserImage = AppData.LoggedInUser.getString("rPic", "");;
        byte[] imgBytesData = Base64.decode(rUserImage, Base64.DEFAULT);
        myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        holder.studentIcon.setImageBitmap(myBitmap);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSViewGatepassFragmentInteraction(v.getContext(), holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView GP;
        public final TextView inTime;
        public final TextView outTime;
        public final TextView setStatus;
        public final ImageView studentIcon;
        public GatepassListViewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            GP = (TextView) view.findViewById(R.id.GP);
            setStatus = (TextView) view.findViewById(R.id.setStatus);
            inTime = (TextView) view.findViewById(R.id.inTime);
            outTime = (TextView) view.findViewById(R.id.outTIme);
            studentIcon = (ImageView) view.findViewById(R.id.ivIcon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + setStatus.getText() + "'";
        }
    }
}

