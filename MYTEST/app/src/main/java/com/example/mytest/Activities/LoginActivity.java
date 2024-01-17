package com.example.mytest.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.DbQuery;
import com.example.mytest.MyCompleteListener;
import com.example.mytest.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button btnlogin;
    private TextView btnsignup;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;
    private RelativeLayout btngSign;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=104;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        btnlogin=findViewById(R.id.btnLogIn);
        btnsignup=findViewById(R.id.btnSignUp);
        btngSign=findViewById(R.id.btng_sign);

        mAuth=FirebaseAuth.getInstance();

        progressDialog=new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText=progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        //Configure Google Sign In
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData())
                {
                    login();
                }
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        btngSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }
    private boolean validateData(){
        boolean status=false;
        if(email.getText().toString().isEmpty()){
            email.setError("Enter E-Mail ID");
            return false;
        }
        if(pass.getText().toString().isEmpty()){
            pass.setError("Enter Password");
            return false;
        }
        return true;
    }

 private void login(){
     progressDialog.show();
     String inputEmail = email.getText().toString().trim();
     String inputPassword = pass.getText().toString().trim();

     // Kiểm tra xem tài khoản có tồn tại trong Firestore hay không
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     db.collection("USERS").whereEqualTo("EMAIL_ID", inputEmail)
             .get()
             .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     if (task.isSuccessful()) {
                         if (!task.getResult().isEmpty()) {
                             // Nếu tài khoản tồn tại, tiếp tục đăng nhập
                             mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                                     .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                         @Override
                                         public void onComplete(@NonNull Task<AuthResult> task) {
                                             if (task.isSuccessful()) {
                                                 Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                                 DbQuery.loadData(new MyCompleteListener() {
                                                     @Override
                                                     public void onSuccess() {
                                                         progressDialog.dismiss();
                                                         Intent intent;
                                                         if (inputEmail.equals("admin123@gmail.com")) {
                                                             intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                         } else {
                                                             intent = new Intent(LoginActivity.this, MainActivity.class);
                                                         }
                                                         startActivity(intent);
                                                         finish();
                                                     }

                                                     @Override
                                                     public void onFailure() {
                                                         progressDialog.dismiss();
                                                         Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                                                 Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                             }
                                         }
                             });
                         } else {
                             // Nếu tài khoản không tồn tại, hiển thị thông báo lỗi
                             progressDialog.dismiss();
                             Toast.makeText(LoginActivity.this, "Account does not exist.", Toast.LENGTH_SHORT).show();
                         }
                     } else {
                         // Xử lý lỗi khi truy vấn Firestore
                         progressDialog.dismiss();
                         Toast.makeText(LoginActivity.this, "Error checking account existence: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
             });
 }

    private void googleSignIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){

        progressDialog.show();
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this,"Google Sign In Success",Toast.LENGTH_SHORT).show();
                            FirebaseUser user=mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                DbQuery.createUserData(user.getEmail(), user.getDisplayName(), new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        DbQuery.loadData(new MyCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                progressDialog.dismiss();
                                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                            }

                                            @Override
                                            public void onFailure() {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure() {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                DbQuery.loadData(new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        progressDialog.dismiss();
                                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }

                                    @Override
                                    public void onFailure() {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
/*   private void login(){
        progressDialog.show();
        String inputEmail = email.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(inputEmail, pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                            DbQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    Intent intent;
                                    if(inputEmail.equals("admin123@gmail.com")) {
                                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/