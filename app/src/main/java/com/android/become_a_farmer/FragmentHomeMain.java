package com.android.become_a_farmer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.become_a_farmer.service.AuthenticationService;
import com.android.become_a_farmer.service.GetRecommendRegionService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentHomeMain extends Fragment {
    private android.view.View view;
    private static FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter rAdapter;
    private TextView txt_name;
    private UserDTO userDTO;
    private SendRatingsData sendRatingsData;
    private String email;
    private TextView txt_preference;
    public static Context context_main;
    private GetRecommendRegionService getRecommendRegionService;
    private AuthenticationService authenticationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_main, container, false);
        context_main = getContext();
        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_lst);
        rAdapter = new RecyclerViewAdapter(getContext());
        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_preference = (TextView) view.findViewById(R.id.txt_preference);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        getRecommendRegionService = new GetRecommendRegionService(db, rAdapter);
        authenticationService = new AuthenticationService();
//
//        if (decoration != null) {
//            recyclerView.removeItemDecoration(decoration);
//        }

        // 최초 실행 여부를 판단
        // isFirst : true => 최초 실행
        // isFirst : false => 최초 실행 아님
        email = authenticationService.getUserEmail();
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", true);

        // 로그인 -> 취향 분석 ok 클릭 -> 취향 분석 화면으로 넘어감
//        Log.d("checkFirstRun", String.valueOf(first));


        if ((first) && (email != null)){ // 최초 실행 && 로그인 한 경우
            // 취향 분석 화면으로 넘어가기 위해 사용자에게 다이얼로그 띄움
            // 확인 -> 취향 분석 화면으로 넘어감
            // 다음에 -> 그대로
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("취향 분석을 위한 화면으로 이동하시겠습니까?");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit();

                    Intent intent = new Intent(getActivity().getApplicationContext(), ChoiceAge.class);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("다음에 할게요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        // 사용자의 추천 지역 화면에 뿌림
        if (email != null){
            txt_preference.setVisibility(View.VISIBLE);
            setUserName(email, txt_name);   // 이름 화면에 표시
            // 추천 지역 화면에 뿌림
            getRecommendRegionService.getRecommendRegion(email);



//            DocumentReference docRef = db.collection("ratings").document(email);
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {    // -> 만약 사용자가 리뷰를 등록하지 않았다면, 사용자 기반 추천 시스템 실행할 수 없음
//                            findUserBasedRecommendRegions();
//                        }
//                    } else {
//                        // 서버에서 값을 가져오지 못할 경우 처리
//
//                    }
//                }
//            });

        }

        // 취향 분석 화면으로 넘어가기
        txt_preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_preference.getVisibility() == View.VISIBLE){
                    Intent intent = new Intent(getActivity(), ChoiceAge.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    void findUserBasedRecommendRegions(){
        // 사용자 기반 추천 시스템 돌아감
        sendRatingsData = new SendRatingsData(db, email);
        sendRatingsData.getRatingFromDB();
    }


    // 사용자 이름 ui에 표시
    private void setUserName(String email, TextView txt_name){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String name;
                    try{
                        name = document.getData().get("name").toString();
                        txt_name.setText(name);

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

}