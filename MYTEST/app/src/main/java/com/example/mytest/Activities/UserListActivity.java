package com.example.mytest.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.Adapters.UserAdapter;
import com.example.mytest.DbQuery;
import com.example.mytest.MyCompleteListener;
import com.example.mytest.R;


public class UserListActivity extends AppCompatActivity  {

    private RecyclerView userView;
    private Toolbar toolbar;
    private UserAdapter adapter;
    private Dialog progressDialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userView = findViewById(R.id.recyclerView);


        progressDialog = new Dialog(UserListActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        progressDialog.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        userView.setLayoutManager(layoutManager);

        DbQuery.loadUserData(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                adapter = new UserAdapter(DbQuery.g_usersData);
                userView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(UserListActivity.this, "Something went wrong!Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()== android.R.id.home){
            UserListActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
