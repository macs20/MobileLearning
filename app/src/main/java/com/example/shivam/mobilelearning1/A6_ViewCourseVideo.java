package com.example.shivam.mobilelearning1;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class A6_ViewCourseVideo extends BaseActivity {

    private static final String TAG = "ViewCourseVideoActivity";
    Button playButton;
    VideoView videoView;
    android.widget.MediaController mediaController;
    //String videoDownloadURL = "https://firebasestorage.googleapis.com/v0/b/mobilelearning1-576b3.appspot.com/o/Courses%2FJava%20-%20Computer%20Science%2F1-Introduction%2FJava%20Programming%20Tutorial%20-%201%20-%20Installing%20the%20JDK.3gp?alt=media&token=c798fe3c-0017-44b4-9dc5-77d868eaf989";
    Toolbar videoToolbar;
    Bundle videoBundle;
    String videoPath;
    String pdfPath;
    String courseName;
    String topicName;
    ArrayList<String> topicList;
    ArrayList<String> otherTopicsList;
    String nextTopic;
    Button nextVideoButton;
    Button viewPdfBtn;
    LinearLayout videoLinearLayout;
    LinearLayout.LayoutParams layoutParams;
    int topicIndex;
    TextView upNxt;
    TextView pdfSuggTxt;
    Boolean isLastVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9_view_course_video);
        videoToolbar = (Toolbar) findViewById(R.id.video_toolbar);
        setSupportActionBar(videoToolbar);
        getSupportActionBar().setTitle("Topic Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        videoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        topicList = new ArrayList<>();
        otherTopicsList = new ArrayList<>();
        isLastVideo = false;

        Log.i(TAG, "gls1"+global_learningStyle_1);
        Log.i(TAG, "gls2"+global_learningStyle_2);

        videoBundle = getIntent().getExtras();
        videoPath = videoBundle.getString("VideoDownloadPath");
        courseName = videoBundle.getString("CourseName");
        topicName = videoBundle.getString("TopicName");
        pdfPath = videoBundle.getString("PdfDownloadPath");
        getSupportActionBar().setTitle(topicName);
        getSupportActionBar().setSubtitle(courseName);
        Log.i(TAG, "Video download path from bundle: " + videoPath);

        videoView = (VideoView) findViewById(R.id.videoView);
        mediaController = new android.widget.MediaController(this);
        playButton = (Button) findViewById(R.id.button_play_video);
        playButton.setBackgroundColor(Color.MAGENTA);
        playButton.setTextColor(Color.WHITE);
        nextVideoButton = (Button) findViewById(R.id.next_video_suggestion_button);
        nextVideoButton.setTextColor(Color.WHITE);
        nextVideoButton.setBackgroundColor(Color.BLUE);
        viewPdfBtn = (Button) findViewById(R.id.view_topic_pdf_2);
        viewPdfBtn.setTextColor(Color.WHITE);
        viewPdfBtn.setBackgroundColor(Color.RED);
        upNxt = (TextView) findViewById(R.id.up_next_txt);
        pdfSuggTxt = (TextView) findViewById(R.id.pdf_sugg_txt);
        videoLinearLayout = (LinearLayout) findViewById(R.id.video_activity_linear_layout);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,20,0,0);

        //get list of all topics
        mRootRef.child("users").child(mUser.getUid()).child("Enrolled_Courses").child("Ongoing").child(courseName).child("CourseTopics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    Log.i(TAG, child.getKey());
                    topicList.add(child.getKey());

                }
                getTopicIndex();
                getLearningStyle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //listener for when video is ready to play
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(A6_ViewCourseVideo.this, "Now playing...", Toast.LENGTH_SHORT).show();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                    playButton.setText("Play");
                }
                else{
                    playButton.setText("Stop");
                    videoView.setVideoPath(videoPath);
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                    videoView.requestFocus();
                    videoView.start();
                }

            }
        });

        //view pdf of the same course
        viewPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pdfIntent = new Intent(getApplicationContext(), A5_ViewCoursePDF.class);
                pdfIntent.putExtra("PdfDownloadPath", pdfPath);
                pdfIntent.putExtra("CourseName", courseName);
                pdfIntent.putExtra("TopicName", topicName);
                startActivity(pdfIntent);
            }
        });

        //view video of next topic in this same current activity
        nextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLastVideo == false){
                    Intent restartVideoIntent = new Intent(A6_ViewCourseVideo.this, A6_ViewCourseVideo.class);
                    restartVideoIntent.putExtra("VideoDownloadPath", videoPath);
                    restartVideoIntent.putExtra("PdfDownloadPath", pdfPath);
                    restartVideoIntent.putExtra("CourseName", courseName);
                    restartVideoIntent.putExtra("TopicName", nextTopic);
                    A6_ViewCourseVideo.this.finish();
                    startActivity(restartVideoIntent);
                }
                else {
                    Intent doneIntent = new Intent(getApplicationContext(), A3_CourseTopicsActivity.class);
                    doneIntent.putExtra("CourseName", courseName);
                    startActivity(doneIntent);
                }

            }
        });
    }

    private void getLearningStyle() {
        //get user learning style
        mRootRef.child("users").child(mUser.getUid()).child("Learning Styles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator );
                String learning_style_3 = map.get("learning_style_3");
                String learning_style_4 = map.get("learning_style_4");
                global_learningStyle_1 = map.get("learning_style_1");
                global_learningStyle_2 = map.get("learning_style_2");
                global_learningStyle_3 = map.get("learning_style_3");
                global_learningStyle_4 = map.get("learning_style_4");
                if(learning_style_3.equals("3_Verbal Learner")){
                    viewPdfBtn.setVisibility(View.INVISIBLE);
                    pdfSuggTxt.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "verbal learner, no pdf recommendation needed");
                }
                if(learning_style_4.equals("4_Global Learner")){
                    upNxt.setVisibility(View.INVISIBLE);
                    nextVideoButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTopicIndex() {
        //get index of topic selected
        topicIndex = topicList.indexOf(topicName);
        Log.i(TAG, "topic index" + ": " + topicIndex);
        if(topicIndex == topicList.size()-1){
            nextTopic = "No next topics, this is the last topic video of the course";
            //nextVideoButton.setVisibility(View.INVISIBLE);
            upNxt.setVisibility(View.INVISIBLE);
            isLastVideo = true;
        }
        else{
            nextTopic = topicList.get(topicIndex+1);
        }
        Log.i(TAG, "next topic: " + nextTopic);
        nextVideoButton.setText(nextTopic + " (Video)");
        viewPdfBtn.setText(topicName + " (PDF)");

        setOtherVideoSuggestions(); //for global learners
        if(isLastVideo == true){
            nextVideoButton.setText("DONE");
        }
    }

    //for global learners
    private void setOtherVideoSuggestions(){
        otherTopicsList = topicList;
        otherTopicsList.remove(topicIndex);
        for(int i=0; i<otherTopicsList.size();i++){
            final Button mButton = new Button(getApplicationContext());
            mButton.setText(otherTopicsList.get(i));
            mButton.setBackgroundColor(Color.DKGRAY);
            mButton.setTextColor(Color.WHITE);
            mButton.setLayoutParams(layoutParams);
            videoLinearLayout.addView(mButton, layoutParams);
            Log.i(TAG, "button " + otherTopicsList.get(i) + " added");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, mButton.getText().toString());
                    Intent courseDetailsIntent = new Intent(getApplicationContext(), A4_TopicFiles.class);
                    courseDetailsIntent.putExtra("TopicName", mButton.getText().toString());
                    courseDetailsIntent.putExtra("CourseName", courseName);
                    startActivity(courseDetailsIntent);
                }
            });
        }
    }
}
