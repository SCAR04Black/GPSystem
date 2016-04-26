package q_2.nu_gatepass;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pradumn K Mahanta on 26-04-2016.
 **/

public class GetImage extends AsyncTask<String, String, Bitmap>{

    private ProgressDialog pDialog;
    final Context mContext;
    ImageView imgView;

    public GetImage(Context mContext, ImageView imgView){
        this.mContext = mContext;
        this.imgView = imgView;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(this.mContext);
        pDialog.setMessage("Fetching Image...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap myBitmap;
        try {
            URL url = new URL("http://gatepass.esy.es/getimage.php?user_name="+params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            byte[] imgBytesData = Base64.decode(input.toString(), Base64.DEFAULT);
            myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        } catch (IOException e) {
            return null;
        }
        return myBitmap;
    }

    protected void onPostExecute(Bitmap myBitmap) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        this.imgView.setImageBitmap(myBitmap);
    }
}
