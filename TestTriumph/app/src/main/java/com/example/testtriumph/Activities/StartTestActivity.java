package com.example.testtriumph.Activities;

import static com.example.testtriumph.DbQuery.g_catList;
import static com.example.testtriumph.DbQuery.loadQuestions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testtriumph.DbQuery;
import com.example.testtriumph.MyCompleteListener;
import com.example.testtriumph.R;

public class StartTestActivity extends AppCompatActivity {

    private TextView catName,testNo,totalQ,bestScore,time;
    private Button btnStart;
    private ImageView btnBack,btnAdd,btnReset;
    private Dialog progressDialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        init();

        progressDialog=new Dialog(StartTestActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText=progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        loadQuestions(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                setData();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(StartTestActivity.this, "Something went wrong!Please try again.",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void init(){
        catName=findViewById(R.id.st_cat_name);
        testNo=findViewById(R.id.st_test_no);
        totalQ=findViewById(R.id.st_total_ques);
        bestScore=findViewById(R.id.st_best_score);
        time=findViewById(R.id.st_time);
        btnStart=findViewById(R.id.btnStart_test);
        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTestActivity.this.finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (DbQuery.g_quesList.isEmpty()) {
                    // No questions were loaded, show a message to the user
                    Toast.makeText(StartTestActivity.this, "There are no questions yet, please click the Add Question button to add more", Toast.LENGTH_LONG).show();
                } else {*/
                    // Questions were loaded successfully, navigate to QuestionsActivity
                    Intent intent = new Intent(StartTestActivity.this, QuestionsActivity.class);
                    startActivity(intent);
                    finish();
              //  }
            }
        });
    }

    private void setData(){
        catName.setText(g_catList.get(DbQuery.g_selected_cat_index).getName());
        testNo.setText("Test No. " + String.valueOf(DbQuery.g_selected_test_index+1));
        totalQ.setText(String.valueOf(DbQuery.g_quesList.size()));
        bestScore.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTopScore()));
        time.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()));
    }



}