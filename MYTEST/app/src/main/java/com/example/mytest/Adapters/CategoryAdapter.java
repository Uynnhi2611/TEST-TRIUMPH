package com.example.mytest.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import com.example.mytest.Activities.TestActivity;
import com.example.mytest.DbQuery;
import com.example.mytest.Models.CategoryModel;
import com.example.mytest.Models.TestModel;
import com.example.mytest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<CategoryModel> cat_list;

    public CategoryAdapter(List<CategoryModel> cat_list){
        this.cat_list=cat_list;
    }

    @Override
    public int getCount() {
        return cat_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View myView;
        if(view== null){
            myView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cat_item_layout,viewGroup,false);
        }else{
            myView=view;
        }

        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQuery.g_selected_cat_index = i;
                Intent intent = new Intent(v.getContext(), TestActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        TextView catName= myView.findViewById(R.id.catName);
        TextView noOfTests=myView.findViewById(R.id.no_of_tests);
        ImageView catImage = myView.findViewById(R.id.catImage); // Get the ImageView by its ID

        catName.setText(cat_list.get(i).getName());
        noOfTests.setText(String.valueOf(cat_list.get(i).getNoOfTests())+" Tests");

        // Tải và hiển thị hình ảnh từ URL
        String imageUrl = cat_list.get(i).getImage();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(viewGroup.getContext()).load(uri.toString()).into(catImage);
            }
        });

        return myView;
    }



}
