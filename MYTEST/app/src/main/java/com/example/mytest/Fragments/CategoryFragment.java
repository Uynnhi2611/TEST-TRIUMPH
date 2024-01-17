package com.example.mytest.Fragments;

import static com.example.mytest.DbQuery.g_testList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mytest.Activities.MainActivity;
import com.example.mytest.Adapters.CategoryAdapter;
import com.example.mytest.DbQuery;
import com.example.mytest.Models.TestModel;
import com.example.mytest.R;

import java.util.ArrayList;



public class CategoryFragment extends Fragment {

    private GridView catView;
    private AutoCompleteTextView searchView;
    public CategoryFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        catView = view.findViewById(R.id.cat_Grid);
        CategoryAdapter categoryAdapter = new CategoryAdapter(DbQuery.g_catList);
        catView.setAdapter(categoryAdapter);

        searchView=view.findViewById(R.id.btnFind);

        ArrayList<String> dataSet = new ArrayList<>();

        for (TestModel test : g_testList) {
            String suggestion = test.getTestID() +" "+ test.getTestName();
            dataSet.add(suggestion);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_dropdown_item_1line, dataSet);
        searchView.setAdapter(adapter);

        // Set the threshold here
        searchView.setThreshold(1);

        return view;
    }
}
 /*  searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
 /*// Hiển thị testPath bằng AlertDialog
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Test Path")
                            .setMessage(testPath)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();

                    // Hiển thị g_foundTestList bằng AlertDialog
                    StringBuilder foundTestsStr = new StringBuilder();
                    for (TestModel test : foundTests) {
                        foundTestsStr.append(test.getTestID()).append(" ").append(test.getTestName()).append("\n");
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Found Tests")
                            .setMessage(foundTestsStr.toString())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
*/

