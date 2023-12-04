package com.example.testtriumph.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.testtriumph.DbQuery;
import com.example.testtriumph.MyCompleteListener;
import com.example.testtriumph.R;
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
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                            DbQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Choose your role");
                                String[] roles = {"Teacher", "Student"};
                                builder.setItems(roles, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String role = roles[which];
                                        DbQuery.createUserData(user.getEmail(), user.getDisplayName(), role, new MyCompleteListener() {
                                            @Override
                                            public void onSuccess() {

                                                DbQuery.loadData(new MyCompleteListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
                                            @Override
                                            public void onFailure() {
                                                // Xử lý khi tạo dữ liệu người dùng thất bại
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Something went wrong! Please try again.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                builder.show();
                            }
                            else {
                                DbQuery.loadData(new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}