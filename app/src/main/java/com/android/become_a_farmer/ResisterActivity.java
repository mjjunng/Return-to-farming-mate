package com.android.become_a_farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.become_a_farmer.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
//    private ImageButton btn_prev;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnRegister.setOnClickListener(this);
    }
    // 다른 화면 터치 시 키보드 내림
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.resisterTxtId.getWindowToken(), 0);
        return true;
    }

    // 사용자 정보 firestore에 저장(document : email)
    private void addUserData(String email, String pwd, String name, String nickname){
        // 사용자 데이터(userDTO) 생성한 후 fireStore에 저장
        UserDTO userDTO = new UserDTO(email, pwd, name, nickname);
        db.collection("users").document(email).set(userDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 데이터 추가 성공 시
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add user data", "Error writing document", e);
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                final String email = binding.resisterTxtId.getText().toString().trim();
                final String pwd = binding.resisterTxtPwd.getText().toString().trim();
                final String check_pwd = binding.resisterTxtCheckPwd.getText().toString().trim();
                final String name = binding.resisterTxtName.getText().toString().trim();
                final String nickname = binding.txtNickname.getText().toString().trim();

                // 사용자 인증
                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(ResisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    addUserData(email, pwd, name, nickname);
                                    Toast.makeText(ResisterActivity.this, "회원 가입을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(ResisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        });
                break;

            case R.id.btn_prev:
                Intent intent = new Intent(ResisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
