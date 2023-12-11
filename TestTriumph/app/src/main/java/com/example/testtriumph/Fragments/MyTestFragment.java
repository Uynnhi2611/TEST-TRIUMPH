package com.example.testtriumph.Fragments;


//import static com.example.testtriumph.DbQuery.deleteCategory;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.testtriumph.Activities.MainActivity;
import com.example.testtriumph.Adapters.CategoryAdapter;
import com.example.testtriumph.DbQuery;
import com.example.testtriumph.Models.CategoryModel;
import com.example.testtriumph.MyCompleteListener;
import com.example.testtriumph.R;

public class MyTestFragment extends Fragment {

    private GridView catView;
    private ImageView btnAdd,btnDel;
    EditText inputCategoryName;
    private CategoryAdapter adapter ;
    private Button btnUpload;
    public MyTestFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_test, container, false);
        Toolbar toolbar =getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Test");

        catView=view.findViewById(R.id.cat_Grid);
        // Gọi phương thức loadMyCategories
        DbQuery.loadMyCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                // Tạo một đối tượng CategoryAdapter mới
                adapter=new CategoryAdapter(DbQuery.g_my_catList);
                // Gọi phương thức setAdapter cho biến catView
                catView.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd=view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        btnDel=view.findViewById(R.id.btnDelete);
      /*  btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCatgory();
            }
        });*/

        return  view;

    }
    private void addCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.add_category_dialog_layout, null);
        btnUpload = view.findViewById(R.id.btnUpload);
        inputCategoryName = view.findViewById(R.id.inputCategoryName);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = inputCategoryName.getText().toString();
                if (!categoryName.isEmpty()) {
                    int noOfTest = 0; // Mặc định là 0
                    // Tạo một đối tượng CategoryModel mới
                    CategoryModel newCategory = new CategoryModel("", categoryName, noOfTest);
                    // Thêm đối tượng này vào danh sách danh mục
                    DbQuery.g_my_catList.add(newCategory);
                    DbQuery.g_catList.add(newCategory);
                    // Cập nhật giao diện người dùng ngay lập tức
                    adapter.notifyDataSetChanged();
                    // Cập nhật dữ liệu trên Firebase
                    DbQuery.createCategory(newCategory, new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(view.getContext(), "Add Successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(view.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(view.getContext(), "Enter category name!", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

   /* private void deleteCatgory(){
        // Kiểm tra xem danh sách có rỗng hay không
        if (DbQuery.g_my_catList.isEmpty()) {
            Toast.makeText(getContext(), "There are no categories! Please add a category.", Toast.LENGTH_SHORT).show();
        } else {
            // Tạo một hộp thoại mới
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select a category to delete.");

            // Tạo một danh sách các CAT_NAME
            String[] catNames = new String[DbQuery.g_my_catList.size()];
            for (int i = 0; i < DbQuery.g_my_catList.size(); i++) {
                catNames[i] = DbQuery.g_my_catList.get(i).getName();
            }

            builder.setItems(catNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Xóa danh mục khi một CAT_NAME được chọn
                    deleteCategory(DbQuery.g_my_catList.get(which).getDocID(), new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Delete Successful!", Toast.LENGTH_SHORT).show();
                            // Cập nhật UI sau khi xóa thành công
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            // Hiển thị hộp thoại
            builder.show();
        }
    }*/


}
