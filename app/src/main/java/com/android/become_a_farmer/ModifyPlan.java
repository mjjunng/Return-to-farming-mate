package com.android.become_a_farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ModifyPlan extends AppCompatActivity {
    private android.view.View view;
    private LinearLayout ll;
    private Button btn_ok;
    private CheckBox chbox_quinong;
    private CheckBox chbox_quichon;
    private CheckBox chbox_nature;
    private CheckBox chbox_culture;
    private static FirebaseFirestore db;
    private ArrayList<String> checkedKeywords;
    private String str_checkedKeywords = "";
    private String email;
    private String planningType;
    private String preferredType;
    private String recommendRegions;
    private Socket client;
    private String SERVER_IP = BuildConfig.SERVER_IP;
    private int PORT = 9090;
    private InputStream is;
    private DataOutputStream dos;
    private int gubun;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_plan);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        chbox_quinong = (CheckBox) findViewById(R.id.chbox_quinong);
        chbox_quichon = (CheckBox) findViewById(R.id.chbox_quichon);
        chbox_nature = (CheckBox) findViewById(R.id.chbox_nature);
        chbox_culture = (CheckBox) findViewById(R.id.chbox_culture);

        checkedKeywords = new ArrayList<>();
        email= getUserEmail();

        // db??? ????????? ????????? ???????????? ????????? ??????
        // ui ???????????? ?????? ?????????
        String keywords = getKeyword();
        setKeywordCheckboxUI(keywords);

        chbox_quinong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planningType = "quinong";
            }
        });

        chbox_quichon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planningType = "quichon";
            }
        });

        chbox_nature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferredType = "nature";
            }
        });

        chbox_culture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferredType = "culture";
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ?????? ????????????
                updatePlanningType();
                updatePreferredType();
                // ????????? ????????? -> ?????????
                str_checkedKeywords = listToString(checkedKeywords);
                updateUserCheckedKeywords();
                connect();  // ?????? ????????? ????????????

                // user_main ???????????? ?????????(activity -> fragment ??????)
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();

                ft.replace(R.id.frameLayout, new user_main());
                ft.commit();


            }
        });


    }

    // SharedPreference??? ????????? keyword ????????????
    public String getKeyword(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("keywords", null);
    }

    public void setKeywordCheckboxUI(String keywords){
        ll = (LinearLayout) findViewById(R.id.ll3);
        if(keywords!= null){
            String[] s = keywords.split(",");
            for (int i=0; i<s.length; i+=2) {
                CheckBox ch1 = new CheckBox(this);
                ch1.setText(s[i]);
                ch1.setId(i);

                int idx = i + 1;
                CheckBox ch2 = new CheckBox(this);
                ch2.setText(s[idx]);
                ch2.setId(idx);

                LinearLayout addll = new LinearLayout(this);
                addll.setOrientation(LinearLayout.HORIZONTAL);
//                LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);

                addll.addView(ch1);
                addll.addView(ch2);
                ll.addView(addll);

                ch1.setOnClickListener(getOnClickSomething(ch1));
                ch2.setOnClickListener(getOnClickSomething(ch2));
            }
        }
    }

    View.OnClickListener getOnClickSomething(final Button button){
        return new View.OnClickListener(){
            // ??? ???????????? ??????????????? ?????? ???????????? ?????? ??????
            public void onClick(View v){    // ???????????? ??? ???????????? ?????? ????????? ???????????? ??????
                if (!checkedKeywords.contains(button.getText().toString())){
                    checkedKeywords.add(button.getText().toString());
                } else { // ????????? ???????????? ?????? ???????????? ?????? ?????? ??????????????? ??????
                    checkedKeywords.remove(button.getText().toString());
                }
            }
        };
    }
    public void updatePlanningType(){
        if (email != null){
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(email);
            userRef.update("planningType", planningType)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("error add user age", e);
                        }
                    });
        }else{
            Toast.makeText(ModifyPlan.this, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();

        }
    }

    public void updatePreferredType(){
        if (email != null){
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(email);
            userRef.update("preferredType", preferredType)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("update", "Dddddd");
                        }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("error add user age", e);
                        }
                    });
        }else{
            Toast.makeText(ModifyPlan.this, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();

        }

    }

    public void updateUserCheckedKeywords(){
        if (email != null){
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(email);
            userRef.update("prefferdKeywords", str_checkedKeywords)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("error add user age", e);
                        }
                    });
        }else{
            Toast.makeText(ModifyPlan.this, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();

        }
    }
    void connect(){
        Thread sendKeywords = new Thread(){
            public void run(){
                try{    // ?????? ??????
                    client = new Socket(SERVER_IP, PORT);
                    dos = new DataOutputStream(client.getOutputStream());

                    gubun = 1;
                    dos.writeUTF(Integer.toString(gubun));
                    dos.flush();

                } catch (IOException e){
                    e.printStackTrace();
                }

                try{
                    dos.writeUTF(str_checkedKeywords);
                    dos.flush();
                } catch (Exception e) {

                }

                try{
                    byte[] byteArr = new byte[1024];    // ?????? ????????? ???????????? ????????????
                    is = client.getInputStream();
                    int readByteCount = is.read(byteArr);
                    recommendRegions = new String(byteArr, 0, readByteCount, "UTF-8");
//                        Log.d("regions", recommendRegions);
                    // ????????? ?????? ????????????(?????? ????????? ????????? ??????)
                    updateUserDataRegions();
                    dos.close();
                    is.close();
                    client.close();
                } catch (Exception e) {

                }
            }
        };
        sendKeywords.start();
    }
    public void updateUserDataRegions(){
        if (email != null){
            DocumentReference userRef = db.collection("users").document(email);
            String[] recommendRegionsArray = recommendRegions.split(",");

            if (0 < recommendRegionsArray.length){
                for (int i=0; i<recommendRegionsArray.length; i++) {
                    userRef.update("recommendRegions", FieldValue.arrayUnion(recommendRegionsArray[i]));
                }
            }
        }else{
            Toast.makeText(ModifyPlan.this, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();

        }
    }

    // ?????? ???????????? ????????? ????????????
    public String getUserEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            return user.getEmail();
        }
        return null;
    }

    // arraylist -> string
    public String listToString(ArrayList<String> arr) {
        String res = "";
        for (String s : arr) {
            res += s + ",";
        }
        // ??? ????????? , ??????
        res = res.substring(0, res.length() - 1);
        return res;
    }
}