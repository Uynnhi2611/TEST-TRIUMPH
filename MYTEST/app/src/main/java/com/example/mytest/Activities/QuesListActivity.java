package com.example.mytest.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;

import com.example.mytest.Adapters.CategoryAdapter;
import com.example.mytest.Adapters.QuesListAdapter;
import com.example.mytest.DbQuery;
import com.example.mytest.R;

public class QuesListActivity extends AppCompatActivity {

    private GridView catView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ques_list);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Questions List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        catView = findViewById(R.id.cat_Grid);
        QuesListAdapter quesListAdapter = new QuesListAdapter(DbQuery.g_catList);
        catView.setAdapter(quesListAdapter);

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()== android.R.id.home){
            QuesListActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}