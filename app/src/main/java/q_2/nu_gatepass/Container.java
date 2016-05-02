package q_2.nu_gatepass;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Container extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SFragmentViewGatepass.OnSViewGatepassFragmentInteractionListener,
        WFragmentViewGatepass.OnWViewGatepassFragmentInteractionListener, SFragmentUserProfile.SFragmentUserProfileInteractionListener,
        WFragmentUserProfile.WFragmentUserProfileInteractionListener, WFragmentViewUser.WFragmentViewUserInteractionListener,
        SFragmentApplyGatepass.SFragmentApplyGatepassFragmentInteractionListener{

    MenuItem mPreviousMenuItem;
    String mUserType, mUserName;
    AppData isLoggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        String acc_type, acc_name;

        isLoggedInUser = new AppData(getApplicationContext());
        if(!isLoggedInUser.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState != null)
        {
            AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(this);
            acc_type = AppData.LoggedInUser.getString("rUserType", "");
            acc_name = AppData.LoggedInUser.getString("rFullName", "");
            Log.d("Bundle : ", "Restored.");
            mUserType = acc_type;
            mUserName = acc_name;
        }
        else
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(this);
                acc_type = AppData.LoggedInUser.getString("rUserType", "");
                acc_name = AppData.LoggedInUser.getString("rFullName", "");
                Log.d("Bundle : ", "Restored.");
                mUserType = acc_type;
                mUserName = acc_name;
            }
            else {
                acc_type = extras.getString("ACC_TYPE");
                acc_name = extras.getString("ACC_HOLDER");
                mUserType = acc_type;
                mUserName = acc_name;
                Log.d("Bundle : ", "Did not restore anything.");
            }
        }

        if(mUserType.equals("WARDEN")){
                WFragmentUserProfile wStartFragment = new WFragmentUserProfile();
                getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, wStartFragment).commit();
        }else{
                SFragmentUserProfile sStartFragment = new SFragmentUserProfile();
                getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, sStartFragment).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        expandNavigationView(acc_type);

        View nav_header = navigationView.inflateHeaderView(R.layout.navigation_drawer_header);
        ImageView image = (ImageView) nav_header.findViewById(R.id.imageProfile);
        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String rUserImage = AppData.LoggedInUser.getString("rPic", "");
        byte[] imgBytesData = Base64.decode(rUserImage, Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
        image.setImageBitmap(myBitmap);

        TextView name = (TextView) nav_header.findViewById(R.id.textViewName);
        name.setText(mUserName);
        TextView email = (TextView) nav_header.findViewById(R.id.textViewEmail);
        email.setText(mUserType);
    }

    private void expandNavigationView(String acc_type) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear();
        String [] menu_list;
        if (acc_type.equals("STUDENT")) {
            menu_list = new String[] {"Your Profile","Apply Gatepass", "Check Gatepass Status"};

            for (int i = 0, menu_listLength = menu_list.length; i < menu_listLength; i++) {
                String aMenu_list = menu_list[i];
                menu.add(R.id.group_menu, i, Menu.NONE, aMenu_list).setIcon(R.mipmap.ic_launcher);
            }
        }
        else if (acc_type.equals("WARDEN")) {
            menu_list = new String[] {"Your Profile", "Respond to request", "View User"};
            for (int i = 0, menu_listLength = menu_list.length; i < menu_listLength; i++) {
                String aMenu_list = menu_list[i];
                menu.add(R.id.group_menu, i, Menu.NONE, aMenu_list).setIcon(R.mipmap.ic_launcher);
            }
        }

        menu.setGroupCheckable(R.id.group_menu, false, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mPreviousMenuItem != null) {
                mPreviousMenuItem.setChecked(false);
            if (mPreviousMenuItem == item)
                return true;
        }
        mPreviousMenuItem = item;
        Fragment fragment = null;
        Class fragmentClass;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /** Add cases for student block
         *  0 = UserProfile
         *  1 = Apply Gatepass
         *  2 = View Gatepass
         **/

        if (mUserType.equals("STUDENT")) {
            switch (id) {
                case 0:
                    fragmentClass = SFragmentUserProfile.class;
                    break;
                case 1:
                    fragmentClass = SFragmentApplyGatepass.class;
                    break;
                case 2:
                    fragmentClass = SFragmentViewGatepass.class;
                    break;
                default:
                    fragmentClass = SFragmentUserProfile.class;
                    break;
            }
        } else {
            /** Add cases for student block
             *  0 = Profile
             *  1 = Respond to request
             *  2 = View User1
             */
            switch (id) {
                case 0:
                    fragmentClass = WFragmentUserProfile.class;
                    break;
                case 1:
                    fragmentClass = WFragmentViewGatepass.class;
                    break;
                case 2:
                    fragmentClass = WFragmentViewUser.class;
                    break;
                default:
                    fragmentClass = WFragmentUserProfile.class;
                    break;
            }
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFragment, fragment).commit();

        item.setChecked(true);
        setTitle("GatePass System");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onSViewGatepassFragmentInteraction(Context mContext, GatepassListViewItem item) {
        SCustomDialog customDialog = new SCustomDialog(mContext, item);
        customDialog.show();
        Log.d("Custom Dialog", "Opened");
    }

    @Override
    public void onWViewGatepassFragmentInteraction(Context mContext, GatepassListViewItem item) {
        WCustomDialog customDialog = new WCustomDialog(mContext, item);
        customDialog.show();
        Log.d("Custom Dialog", "Opened");
    }

    @Override
    public void onSFragmentUserProfileInteraction(Uri uri) {

    }

    @Override
    public void onWFragmentUserProfileInteraction(Uri uri) {

    }

    @Override
    public void onSFragmentApplyGatepassFragmentInteraction(Uri uri) {

    }

    @Override
    public void onWFragmentViewUserInteractionListener(Context mContext, UserListViewItem uItem) {

    }
}
