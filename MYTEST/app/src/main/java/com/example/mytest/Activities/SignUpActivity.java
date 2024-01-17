package com.example.mytest.Activities;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.mytest.DbQuery;
import com.example.mytest.MyCompleteListener;
import com.example.mytest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity {

    private EditText name,email,pass,confirmPass;
    private Button btnSignUp;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private String emailStr,passStr,confirmPassStr,nameStr;
    String emailPattern = "(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Dialog progressDialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name=findViewById(R.id.username);
        email=findViewById(R.id.emailID);
        pass=findViewById(R.id.password);
        confirmPass=findViewById(R.id.confirm_pass);
        btnSignUp=findViewById(R.id.btnSignUp);
        btnBack=findViewById(R.id.btnBback);
        progressDialog=new Dialog(SignUpActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText=progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Registering user...");

        mAuth= FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(validate()){
                    signupNewUser();
                }
            }
        });

    }
    private boolean validate(){
        nameStr=name.getText().toString().trim();
        passStr=pass.getText().toString().trim();
        emailStr=email.getText().toString().trim();
        confirmPassStr=confirmPass.getText().toString().trim();

        if(nameStr.isEmpty()){
            name.setError("Enter Your Name");
            return false;
        }

        if(emailStr.isEmpty()){
            email.setError("Enter Email ID");
            return false;
        }

        if(passStr.isEmpty()){
            pass.setError("Enter Password");
            return false;
        }
        if(confirmPassStr.isEmpty()){
            pass.setError("Enter Confirm Password");
            return false;
        }


        if(!emailStr.matches(emailPattern)) {
            email.setError("Email name must contain at least one character and one number");
            return false;
        }

        if(passStr.length()<6) {
            pass.setError("Password must contain 6 characters");
        }
        if(passStr.compareTo(confirmPassStr)!=0){
            Toast.makeText(SignUpActivity.this,"Password and confirm Password should be same!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signupNewUser(){

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Sign Up Successfull",Toast.LENGTH_SHORT).show();

                            DbQuery.createUserData(emailStr,nameStr,new MyCompleteListener(){
                                @Override
                                public void onSuccess() {

                                    DbQuery.loadData(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                @Override
                                public void onFailure() {
                                    Toast.makeText(SignUpActivity.this,"Something went wrong!Please Try Again Later!",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
 /*  private void signupNewUser(){
       progressDialog.show();
       String inputEmail = emailStr.trim();
       String inputPassword = passStr.trim();

       // Kiểm tra xem tài khoản có tồn tại trong Firebase Authentication hay không
       mAuth.fetchSignInMethodsForEmail(inputEmail)
               .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                   @Override
                   public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                       boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                       if (!isNewUser) {
                           // Nếu tài khoản tồn tại trong Firebase Authentication, kiểm tra trong Firestore
                           FirebaseFirestore db = FirebaseFirestore.getInstance();
                           db.collection("USERS")
                                   .get()
                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           if (task.isSuccessful()) {
                                               boolean userExists = false;
                                               for (QueryDocumentSnapshot document : task.getResult()) {
                                                   if (!document.getId().equals("TOTAL_USERS") && inputEmail.equals(document.getString("EMAIL_ID"))) {
                                                       userExists = true;
                                                       break;
                                                   }
                                               }
                                               if (userExists) {
                                                   // Nếu tài khoản tồn tại trong Firestore, hiển thị thông báo lỗi
                                                   progressDialog.dismiss();
                                                   Toast.makeText(SignUpActivity.this, "Account already exists.", Toast.LENGTH_SHORT).show();
                                               } else {
                                                   // Nếu tài khoản không tồn tại trong Firestore, tiếp tục tạo dữ liệu người dùng
                                                   DbQuery.createUserData(inputEmail, nameStr, new MyCompleteListener(){
                                                       @Override
                                                       public void onSuccess() {
                                                           DbQuery.loadData(new MyCompleteListener() {
                                                               @Override
                                                               public void onSuccess() {
                                                                   progressDialog.dismiss();
                                                                   Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                   startActivity(intent);
                                                                   finish();
                                                               }

                                                               @Override
                                                               public void onFailure() {
                                                                   progressDialog.dismiss();
                                                                   Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again.",
                                                                           Toast.LENGTH_SHORT).show();
                                                               }
                                                           });
                                                       }

                                                       @Override
                                                       public void onFailure() {
                                                           Toast.makeText(SignUpActivity.this,"Something went wrong! Please try again later!",Toast.LENGTH_SHORT).show();
                                                           progressDialog.dismiss();
                                                       }
                                                   });
                                               }
                                           } else {
                                               // Xử lý lỗi khi truy vấn Firestore
                                               progressDialog.dismiss();
                                               Toast.makeText(SignUpActivity.this, "Error checking account existence: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });
                       } else {
                           // Nếu tài khoản không tồn tại trong Firebase Authentication, tiếp tục đăng ký
                           mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                                   .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                       @Override
                                       public void onComplete(@NonNull Task<AuthResult> task) {
                                           if (task.isSuccessful()) {
                                               Toast.makeText(SignUpActivity.this,"Sign Up Successful",Toast.LENGTH_SHORT).show();

                                               DbQuery.createUserData(inputEmail, nameStr, new MyCompleteListener(){
                                                   @Override
                                                   public void onSuccess() {
                                                       DbQuery.loadData(new MyCompleteListener() {
                                                           @Override
                                                           public void onSuccess() {
                                                               progressDialog.dismiss();
                                                               Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                               startActivity(intent);
                                                               finish();
                                                           }

                                                           @Override
                                                           public void onFailure() {
                                                               progressDialog.dismiss();
                                                               Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again.",
                                                                       Toast.LENGTH_SHORT).show();
                                                           }
                                                       });
                                                   }

                                                   @Override
                                                   public void onFailure() {
                                                       Toast.makeText(SignUpActivity.this,"Something went wrong! Please try again later!",Toast.LENGTH_SHORT).show();
                                                       progressDialog.dismiss();
                                                   }
                                               });
                                           } else {
                                               progressDialog.dismiss();
                                               Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });
                       }
                   }
               });
   }*/