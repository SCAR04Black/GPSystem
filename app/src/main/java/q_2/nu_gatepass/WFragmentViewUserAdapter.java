package q_2.nu_gatepass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pradumn K Mahanta on 01-05-2016.
 **/

public class WFragmentViewUserAdapter extends RecyclerView.Adapter<WFragmentViewUserAdapter.ViewHolder>{

    private final List<UserListViewItem> mValues;
    private final WFragmentViewUser.WFragmentViewUserInteractionListener mListener;

    public WFragmentViewUserAdapter(List<UserListViewItem> items, WFragmentViewUser.WFragmentViewUserInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wuser_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.rFullName.setText(holder.mItem.rFullName);
        holder.rEnrollKey.setText(holder.mItem.rEnrollKey);
        holder.rBatch.setText(holder.mItem.rBatch);

        String sStatus = holder.mItem.rStatus;
        if(Integer.valueOf(sStatus) == 1){
            holder.rStatus.setText("Active");
            holder.rStatus.setTextColor(Color.BLUE);
        }else{
            holder.rStatus.setText("Inactive");
            holder.rStatus.setTextColor(Color.RED);
        }

        Bitmap myBitmap;
        byte[] imgBytesData = Base64.decode(holder.mItem.rPic, Base64.DEFAULT);
        myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        holder.studentIcon.setImageBitmap(myBitmap);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onWFragmentViewUserInteractionListener(view.getContext(), holder.mItem);
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
        public final TextView rFullName;
        public final TextView rEnrollKey;
        public final TextView rBatch;
        public final ImageView studentIcon;
        public final TextView rStatus;
        public UserListViewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            rFullName = (TextView) view.findViewById(R.id.rFullName);
            rEnrollKey = (TextView) view.findViewById(R.id.rEnrollKey);
            rBatch = (TextView) view.findViewById(R.id.rBatch);
            studentIcon = (ImageView) view.findViewById(R.id.studentIcon);
            rStatus = (TextView) view.findViewById(R.id.rStatus);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + rEnrollKey.getText() + "'";
        }
    }
}