package com.example.mytest.Fragments;

import static com.example.mytest.DbQuery.findTest;
import static com.example.mytest.DbQuery.g_testList;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mytest.Activities.MainActivity;
import com.example.mytest.Activities.TestActivity;
import com.example.mytest.Adapters.CategoryAdapter;
import com.example.mytest.DbQuery;
import com.example.mytest.Models.TestModel;
import com.example.mytest.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {

    private GridView catView;
    public CategoryFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        GridView catView = view.findViewById(R.id.cat_Grid);
        CategoryAdapter categoryAdapter = new CategoryAdapter(DbQuery.g_catList);
        catView.setAdapter(categoryAdapter);

        SearchView searchView=view.findViewById(R.id.btnFind);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // User has submitted the search string
                List<TestModel> foundTests = findTest(query, query); // Use the method you provided to find the tests

                if (!foundTests.isEmpty()) {
                    // Tests found, start new activity here
                    for (TestModel foundTest : foundTests) {
                        Intent intent = new Intent(getActivity(), TestActivity.class);
                        intent.putExtra("TEST_PATH", foundTest.getTestPath()); // Pass the test path to the new activity
                        startActivity(intent);
                    }
                } else {
                    // Tests not found, show a message to the user
                    Toast.makeText(getActivity(), "Tests not found", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // User has changed the text. This method can be used to provide autocomplete suggestions.
                return false;
            }
        });

     /*   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // User has submitted the search string
                StringBuilder testListStr = new StringBuilder();
                for (TestModel test : g_testList) {
                    testListStr.append("Test ID: ").append(test.getTestID())
                            .append(", Test Name: ").append(test.getTestName())
                            .append(", Test Path: ").append(test.getTestPath())
                            .append("\n");
                }

                testListStr.append("\nTotal tests: ").append(g_testList.size());

                new AlertDialog.Builder(getActivity())
                        .setTitle("Test List")
                        .setMessage(testListStr.toString())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // User has changed the text. This method can be used to provide autocomplete suggestions.
                return false;
            }
        });*/
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // User has submitted the search string
                TestModel foundTest = findTest(query, query); // Use the method you provided to find the test

                if (foundTest != null) {
                    // Test found, show a message to the user with test details
                    String message = "Test found: \n" +
                            "Test Name: " + foundTest.getTestName() + "\n" +
                            "Test ID: " + foundTest.getTestID() + "\n" +
                            "Test Path: " + foundTest.getTestPath();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                } else {
                    // Test not found, show a message to the user
                    Toast.makeText(getActivity(), "Test not found", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // User has changed the text. This method can be used to provide autocomplete suggestions.
                return false;
            }
        });*/
        return view;
    }
}