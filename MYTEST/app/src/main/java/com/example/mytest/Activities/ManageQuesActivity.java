package com.example.mytest.Activities;

import static com.example.mytest.DbQuery.ANSWERED;
import static com.example.mytest.DbQuery.NOT_VISITED;
import static com.example.mytest.DbQuery.PICK_IMAGE_REQUEST_A;
import static com.example.mytest.DbQuery.PICK_IMAGE_REQUEST_B;
import static com.example.mytest.DbQuery.PICK_IMAGE_REQUEST_C;
import static com.example.mytest.DbQuery.PICK_IMAGE_REQUEST_D;
import static com.example.mytest.DbQuery.PICK_IMAGE_REQUEST_Ques;
import static com.example.mytest.DbQuery.REVIEW;
import static com.example.mytest.DbQuery.UNANSWERED;
import static com.example.mytest.DbQuery.addQuestion;
import static com.example.mytest.DbQuery.deleteQuestion;
import static com.example.mytest.DbQuery.g_catList;
import static com.example.mytest.DbQuery.g_quesList;
import static com.example.mytest.DbQuery.g_selected_cat_index;
import static com.example.mytest.DbQuery.g_selected_ques_index;
import static com.example.mytest.DbQuery.g_selected_test_index;
import static com.example.mytest.DbQuery.g_testList;
import static com.example.mytest.DbQuery.loadQuestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.Adapters.QuestionGridAdapter;
import com.example.mytest.Adapters.QuestionsAdapter;
import com.example.mytest.DbQuery;
import com.example.mytest.Models.QuestionModel;
import com.example.mytest.MyCompleteListener;
import com.example.mytest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.nl.languageid.LanguageIdentification;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ManageQuesActivity extends AppCompatActivity  {

    private RecyclerView questionsView;
    private TextView tvQuesID, catNameTV;
    private Button btnSave;
    private ImageButton btnPrevQues,btnNextQues,btnDrawerClose;
    private ImageView btnQuesList;
    private int quesID;
    private QuestionsAdapter quesAdapter;
    private DrawerLayout drawer;
    private GridView quesListGV;
    private ImageView markImage,imgQues,imgA,imgB,imgC,imgD;
    private QuestionGridAdapter gridAdapter;
    private ImageView btnDelete,btnAdd;
    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ques);

        init();

        loadQuestions(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                // Update the adapter with the loaded questions
                quesAdapter = new QuestionsAdapter(g_quesList);
                questionsView.setAdapter(quesAdapter);

                // Create gridAdapter and set it for quesListGV after the questions are loaded
                gridAdapter = new QuestionGridAdapter(ManageQuesActivity.this, g_quesList.size());
                quesListGV.setAdapter(gridAdapter);
            }

            @Override
            public void onFailure() {
                // Handle the failure case
                Toast.makeText(ManageQuesActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        questionsView.setLayoutManager(layoutManager);


        setSnapHelper();
        setClickListeners();

    }
    private void init(){
        questionsView=findViewById(R.id.questions_view);
        tvQuesID=findViewById(R.id.tv_quesID);
        catNameTV=findViewById(R.id.qa_catName);
        btnSave=findViewById(R.id.btnSave);
        btnPrevQues=findViewById(R.id.btnPrev_ques);
        btnNextQues=findViewById(R.id.btnNext_ques);
        btnQuesList=findViewById(R.id.btnQues_list_grid);
        quesID=0;
        tvQuesID.setText("1/"+String.valueOf(g_quesList.size()));
        catNameTV.setText(g_catList.get(g_selected_cat_index).getName());
        drawer = findViewById(R.id.drawer_layout);
        btnDrawerClose=findViewById(R.id.btnDrawerClose);
        markImage=findViewById(R.id.mark_image);
        quesListGV=findViewById(R.id.ques_list_gv);
        btnAdd=findViewById(R.id.btnAdd);
        btnDelete=findViewById(R.id.btnDelete);

    }
    private void setSnapHelper(){
        SnapHelper snapHelper= new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);

        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view  = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quesID= recyclerView.getLayoutManager().getPosition(view);
                if(g_quesList.get(quesID).getStatus()==NOT_VISITED)
                    g_quesList.get(quesID).setStatus(UNANSWERED);
                if(g_quesList.get(quesID).getStatus()==REVIEW){
                    markImage.setVisibility(View.VISIBLE);
                }else {
                    markImage.setVisibility(View.GONE);
                }

                tvQuesID.setText(String.valueOf(quesID+1)+"/"+String.valueOf(g_quesList.size()));

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }
    private void setClickListeners(){
        btnPrevQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quesID>0) {
                    questionsView.smoothScrollToPosition(quesID -1);
                }
            }
        });
        btnNextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( quesID< g_quesList.size()-1)
                {
                    questionsView.smoothScrollToPosition(quesID+1);
                }

            }
        });
        btnQuesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! drawer.isDrawerOpen(GravityCompat.END)){

                    gridAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        btnDrawerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ID của câu hỏi cần xóa
                String questionId = g_quesList.get(g_selected_ques_index).getqID();

                // Gọi hàm deleteQuestion
                deleteQuestion(questionId, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        // Xóa câu hỏi khỏi danh sách câu hỏi
                        g_quesList.remove(g_selected_ques_index);

                        // Cập nhật adapter với danh sách câu hỏi mới
                        quesAdapter.notifyDataSetChanged();

                        // Cập nhật gridAdapter với số lượng câu hỏi mới
                        gridAdapter = new QuestionGridAdapter(ManageQuesActivity.this, g_quesList.size());
                        quesListGV.setAdapter(gridAdapter);

                        Toast.makeText(ManageQuesActivity.this, "Câu hỏi đã được xóa thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(ManageQuesActivity.this, "Lỗi khi xóa câu hỏi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một AlertDialog để người dùng nhập thông tin câu hỏi
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageQuesActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_ques_dialog, null);
                builder.setView(dialogView);

                // Lấy các view từ dialog
                EditText etQuestion = dialogView.findViewById(R.id.etQuestion);
                EditText etOptionA = dialogView.findViewById(R.id.etA);
                EditText etOptionB = dialogView.findViewById(R.id.etB);
                EditText etOptionC = dialogView.findViewById(R.id.etC);
                EditText etOptionD = dialogView.findViewById(R.id.etD);
                EditText correctAns = dialogView.findViewById(R.id.correctAns);
                imgQues = dialogView.findViewById(R.id.imgQues);
                 imgA = dialogView.findViewById(R.id.imgA);
                 imgB = dialogView.findViewById(R.id.imgB);
                 imgC = dialogView.findViewById(R.id.imgC);
                 imgD = dialogView.findViewById(R.id.imgD);

                // Thêm sự kiện click cho nút tải lên hình ảnh
                imgQues.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_Ques);
                    }
                });

                imgA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_A);
                    }
                });

                imgB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_B);
                    }
                });
                imgC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_C);
                    }
                });
                imgD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_D);
                    }
                });


                builder.setPositiveButton("ADD QUESTIONS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Tạo một câu hỏi mới với thông tin từ người dùng
                        QuestionModel newQuestion = new QuestionModel();
                        newQuestion.setQuestion(etQuestion.getText().toString());
                        newQuestion.setOptionA(etOptionA.getText().toString());
                        newQuestion.setOptionB(etOptionB.getText().toString());
                        newQuestion.setOptionC(etOptionC.getText().toString());
                        newQuestion.setOptionD(etOptionD.getText().toString());
                        newQuestion.setCorrectAns(Integer.parseInt(correctAns.getText().toString()));

                        if (imgQues.getDrawable() == null) {
                            newQuestion.setImgQues(null);
                        } else {
                            // Tạo một tham chiếu đến Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                            // Tạo một tham chiếu đến nơi bạn muốn lưu hình ảnh
                            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            // Tải lên hình ảnh
                            UploadTask uploadTask = imageRef.putFile(filePath);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Khi tải lên thành công, lấy URL của hình ảnh
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Lưu URL của hình ảnh vào câu hỏi mới
                                                    newQuestion.setImgQues(uri.toString());

                                                    // Gọi hàm addQuestion
                                                    addQuestion(newQuestion, new MyCompleteListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Toast.makeText(ManageQuesActivity.this, "Câu hỏi đã được thêm thành công", Toast.LENGTH_SHORT).show();
                                                            // Thông báo cho Adapter về sự thay đổi dữ liệu
                                                            quesAdapter.notifyDataSetChanged();
                                                            // Cập nhật gridAdapter với số lượng câu hỏi mới
                                                            gridAdapter = new QuestionGridAdapter(ManageQuesActivity.this, g_quesList.size());
                                                            quesListGV.setAdapter(gridAdapter);
                                                        }

                                                        @Override
                                                        public void onFailure() {
                                                            Toast.makeText(ManageQuesActivity.this, "Lỗi khi thêm câu hỏi", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý lỗi khi tải lên thất bại
                                            Toast.makeText(ManageQuesActivity.this, "Lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                });

                builder.setNegativeButton("Hủy", null);

                builder.create().show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageQuesActivity.this, "Câu hỏi đã được chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManageQuesActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToQuestion(int position){

        questionsView.smoothScrollToPosition(position);

        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                // Lấy hình ảnh từ dữ liệu
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                // Hiển thị hình ảnh trên ImageView tương ứng
                switch (requestCode) {
                    case PICK_IMAGE_REQUEST_Ques:
                        imgQues.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST_A:
                        imgA.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST_B:
                        imgB.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST_C:
                        imgC.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST_D:
                        imgD.setImageBitmap(bitmap);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}