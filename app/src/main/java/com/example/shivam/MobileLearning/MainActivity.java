package com.example.shivam.MobileLearning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseActivity{
    /*
    APPLICATION ACTIVITY SEQUENCE:
    BaseActivity
    MainActivity
    QuestionnaireScrollingActivity
    DisplayLearningStyleActivity
    Dashboard
    */
    Button proceedToGoogleSignIn;
    Button proceedToQuestionnaire;
    Button proceedToDashboard;
    ImageView profilePic;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser != null){

            imgUri = mUser.getPhotoUrl();
            Log.d(TAG, "Photo Uri: "+ mUser.getPhotoUrl().toString());
            Picasso.with(getApplicationContext()).load(imgUri).resize(50,50).into(profilePic);
            Log.d(TAG, "After Picasso load");

        }
        else{
        }

        proceedToQuestionnaire = (Button) findViewById(R.id.proceed_to_questionnaire_button);
        final Intent proceedToQuestionnaireIntent = new Intent(this, QuestionnaireScrollingActivity.class);
        proceedToQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(proceedToQuestionnaireIntent);
            }
        });

        proceedToGoogleSignIn = (Button) findViewById(R.id.proceed_to_google_signin);
        proceedToGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mGoogleSignInIntent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
                startActivity(mGoogleSignInIntent);
            }
        });

        proceedToDashboard = (Button) findViewById(R.id.button_goToDashboard);
        proceedToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
            }
        });
    }
}
