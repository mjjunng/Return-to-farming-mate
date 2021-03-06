package com.android.become_a_farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RegionInfo extends AppCompatActivity {
    private TextView txt_region_name;
    private TextView txt_region_info;
    private TextView txt_crop_info;
    private TextView title_ex;
    private TextView txt_ex_info;
    private RatingBar ratingBar;
    private FirebaseFirestore db;
    private String regionName;
    private float regionRating;
    private boolean changed = false;
    private TextView title_region;
    private RecyclerItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_info);

        txt_region_name = (TextView) findViewById(R.id.txt_region_name);
        txt_region_info = (TextView) findViewById(R.id.txt_region_info);
        txt_crop_info = (TextView) findViewById(R.id.txt_crop_info);
        title_ex = (TextView) findViewById(R.id.title_ex);
        txt_ex_info = (TextView) findViewById(R.id.txt_ex_info);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        title_region = (TextView) findViewById(R.id.title_region);


        Intent intent = getIntent();
        item = (RecyclerItem) intent.getSerializableExtra("item");

        // ????????? ??? ??????
        String content = title_region.getText().toString();
        SpannableString spannableString = new SpannableString(content);

        String word ="??????";
        int start = 0;
        int end = start + word.length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#23cd87")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        title_region.setText(spannableString);


        regionName = item.getTitle();
        txt_region_name.setText(regionName);
        txt_region_info.setText(item.getIntroduction());
        txt_crop_info.setText(item.getCrop());
        title_ex.setText(item.getExperienceTitle());
        txt_ex_info.setText(item.getExperienceContent());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                regionRating = rating;
                changed = true;
            }
        });
    }

    // ????????? ?????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setTitle(item.getTitle());
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        //????????? ?????? ?????? ?????????
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

        return true;
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (changed){
            storeRating(regionName, regionRating);
        }
    }

    // ???????????? ui?????? ????????? rating ???????????? forestore??? ??????
    public void storeRating(String regionName, float rating){
        String email = getUserEmail();
        db = FirebaseFirestore.getInstance();

        DocumentReference ratingsRef = db.collection("ratings").document(email);
        ratingsRef.update(regionName, rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }})
                // ???????????? ??? ?????? ????????? ????????? ????????? ??? -> ?????? ?????? ???????????? ???
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Map<String, Object> data = new HashMap<>();
                        data.put(regionName, rating);
                        db.collection("ratings").document(email).set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                        Log.w("add rating data", "Error writing document", e);
                                    }
                                });
                    }
                });
    }

    public String getUserEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            return user.getEmail();
        }
        return null;
    }
}