package com.example.testtriumph.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;


import com.example.testtriumph.Activities.MainActivity;
import com.example.testtriumph.Adapters.CategoryAdapter;
import com.example.testtriumph.DbQuery;
import com.example.testtriumph.MyCompleteListener;
import com.example.testtriumph.R;

public class MyTestFragment extends Fragment {

    private GridView catView;
    public MyTestFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar =getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Test");

        catView=view.findViewById(R.id.cat_Grid);
        // Gọi phương thức loadMyCategories
        DbQuery.loadMyCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                // Tạo một đối tượng CategoryAdapter mới
                CategoryAdapter adapter=new CategoryAdapter(DbQuery.g_catList);
                // Gọi phương thức setAdapter cho biến catView
                catView.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        return  view;

    }
}
