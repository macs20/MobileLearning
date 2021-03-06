package com.example.shivam.mobilelearning1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class A2_DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        A2a_DashHomeFragment.OnFragmentInteractionListener {

    Toolbar toolbar = null;
    NavigationView navigationView = null;
    View header;
    private static final String TAG = "DrawerActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Boolean signedIn;
    TextView user_name;
    TextView user_email;
    ImageView user_profile_pic;
    public Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6_1_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header=navigationView.getHeaderView(0);

        user_profile_pic = (ImageView)header.findViewById(R.id.profile_image);
        user_name = (TextView)header.findViewById(R.id.textView_dash_username);
        user_email = (TextView)header.findViewById(R.id.textView_dash_useremail);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in");
                    signedIn = true;
                    imgUri = user.getPhotoUrl();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    signedIn = false;
                }
                // [START_EXCLUDE]
                update_nav_drawer(user);
                // [END_EXCLUDE]
            }
        };

        //[START - SET HOME FRAGMENT AS THE DEFAULT DRAWER FRAGMENT]
        A2a_DashHomeFragment dashHomeFrag = new A2a_DashHomeFragment();
        dashHomeFrag.setArguments(null);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, dashHomeFrag);
        fragmentTransaction.commit();
        //[END Set home fragment as default fragment]
    }

    //Update the navigation drawer
    private void update_nav_drawer(FirebaseUser user) {
        if(signedIn == true){
            Picasso.with(getApplicationContext()).load(imgUri).into(user_profile_pic);
            user_name.setText(user.getDisplayName());
            user_email.setText(user.getEmail());
        }
        else{
            user_name.setText("Guest user");
            user_email.setText("Guest email");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getSupportActionBar().setTitle("Home");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            //On pressing the back button, the application exits
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Log.d(TAG, "Dashboard Home option clicked");
            toolbar.setTitle("Home");
            A2a_DashHomeFragment dashHomeFrag = new A2a_DashHomeFragment();
            dashHomeFrag.setArguments(null);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, dashHomeFrag);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(getApplicationContext(), A2b_DashProfileActivity.class);
            startActivity(profileIntent);

        } else if (id == R.id.nav_recommended) {
            Log.d(TAG, "Recommendations option clicked");
            startActivity(new Intent(getApplicationContext(), A2d_DashRecommended.class));
        } else if (id == R.id.nav_discover) {
            Log.d(TAG, "Discover option clicked");
            toolbar.setTitle("Discover");
            Intent discoverIntent = new Intent(this, A2c1_DashDiscoverActivity.class);
            startActivity(discoverIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
