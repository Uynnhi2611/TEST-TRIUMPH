package com.example.testtriumph.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.GridView;

import com.example.testtriumph.Activities.MainActivity;
import com.example.testtriumph.Adapters.CategoryAdapter;
import com.example.testtriumph.DbQuery;
import com.example.testtriumph.R;

public class CategoryFragment extends Fragment {

    private GridView catView;
    public CategoryFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar =getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        catView=view.findViewById(R.id.cat_Grid);
        CategoryAdapter adapter=new CategoryAdapter(DbQuery.g_catList);
        catView.setAdapter(adapter);

        return  view;

    }
}