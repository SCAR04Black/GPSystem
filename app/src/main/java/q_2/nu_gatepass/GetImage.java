package q_2.nu_gatepass;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pradumn K Mahanta on 26-04-2016.
 **/

public class GetImage extends AsyncTask<String, String, Bitmap>{

    final Context mContext;
    StringBuilder result;
    JSONObject jObj;
    final ImageView imageViewUser;

    public GetImage(Context mContext, ImageView imageViewUser){
        this.mContext = mContext;
        this.imageViewUser = imageViewUser;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap myBitmap = null;
        try {
            String urlfinal = "http://gatepass.esy.es/getimage.php?user_name="+params[0];
            URL url = new URL(urlfinal);
            Log.d("Image URL", urlfinal);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            try {
                jObj = new JSONObject(result.toString());
                Log.d("Image", jObj.getString("u_pic"));

                AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = AppData.LoggedInUser.edit();
                editor.putString(params[0], jObj.getString("u_pic"));
                editor.apply();

                byte[] imgBytesData = Base64.decode(jObj.getString("u_pic"), Base64.DEFAULT);
                myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        } catch (IOException e) {
            return null;
        }


        return myBitmap;
    }

    protected void onPostExecute(Bitmap myBitmap) {

        if(myBitmap != null){
            imageViewUser.setImageBitmap(myBitmap);
            Log.d("Image Set", "TRUE");
            Log.d("Image", myBitmap.toString());
        }else{
            Log.d("Image Set", "FALSE");
        }
    }
}