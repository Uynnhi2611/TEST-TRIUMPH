
package com.example.mytest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.mytest.DbQuery;
import com.example.mytest.MyCompleteListener;
import com.example.mytest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    TextView wel;
    private static int Splash_timeout=3000;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        wel=findViewById(R.id.app_name);

        mAuth=FirebaseAuth.getInstance();

       DbQuery.g_firestore=FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    String currentEmail = currentUser.getEmail();
                    DbQuery.loadData(new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Intent intent;
                            if(currentEmail.equals("admin123@gmail.com")) {
                              //  mAuth.signOut();
                                intent = new Intent(SplashActivity.this, AdminActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(SplashActivity.this, "Something went wrong! Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Intent intent= new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        }, Splash_timeout);


        Animation myanimation= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.animation);
        wel.startAnimation(myanimation);
    }
}
    /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser()!= null){
                    // Đăng xuất người dùng khỏi Firebase
                    mAuth.signOut();
                    // Kiểm tra nếu người dùng đã đăng xuất
                    if(mAuth.getCurrentUser() == null){
                        // Chuyển sang LoginActivity
                        Intent intent= new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }else {
                    Intent intent= new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        }, Splash_timeout);*/