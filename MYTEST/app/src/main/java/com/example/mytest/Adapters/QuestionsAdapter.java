package com.example.mytest.Adapters;



import static com.example.mytest.DbQuery.ANSWERED;
import static com.example.mytest.DbQuery.REVIEW;
import static com.example.mytest.DbQuery.UNANSWERED;
import static com.example.mytest.DbQuery.g_quesList;

import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.mytest.Models.QuestionModel;
import com.example.mytest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> questionsList;

    public QuestionsAdapter(List<QuestionModel> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(i);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView ques,opA,opB,opC,opD;
        private LinearLayout optionA, optionB, optionC, optionD, btnPrevSelected;
        private ImageView imgQues, imgA,imgB,imgC,imgD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.tv_question);
            opA = itemView.findViewById(R.id.tv_A);
            opB = itemView.findViewById(R.id.tv_B);
            opC = itemView.findViewById(R.id.tv_C);
            opD = itemView.findViewById(R.id.tv_D);
            optionA=itemView.findViewById(R.id.optionA);
            optionB=itemView.findViewById(R.id.optionB);
            optionC=itemView.findViewById(R.id.optionC);
            optionD=itemView.findViewById(R.id.optionD);
            imgQues=itemView.findViewById(R.id.imgQues);
            imgA=itemView.findViewById(R.id.imgA);
            imgB=itemView.findViewById(R.id.imgB);
            imgC=itemView.findViewById(R.id.imgC);
            imgD=itemView.findViewById(R.id.imgD);
            btnPrevSelected = null;

        }

        private void setData(final int pos) {
            ques.setText(questionsList.get(pos).getQuestion());
            opA.setText(questionsList.get(pos).getOptionA());
            opB.setText(questionsList.get(pos).getOptionB());
            opC.setText(questionsList.get(pos).getOptionC());
            opD.setText(questionsList.get(pos).getOptionD());

            opA.setMovementMethod(new ScrollingMovementMethod());
            opB.setMovementMethod(new ScrollingMovementMethod());
            opC.setMovementMethod(new ScrollingMovementMethod());
            opD.setMovementMethod(new ScrollingMovementMethod());

            setOptions(optionA,1,pos);
            setOptions(optionB,2,pos);
            setOptions(optionC,3,pos);
            setOptions(optionD,4,pos);

            View.OnClickListener optionAClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionA,1,pos);
                }
            };
            optionA.setOnClickListener(optionAClickListener);
            opA.setOnClickListener(optionAClickListener);
            imgA.setOnClickListener(optionAClickListener);

            View.OnClickListener optionBClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionB,1,pos);
                }
            };
            optionB.setOnClickListener(optionBClickListener);
            opB.setOnClickListener(optionBClickListener);
            imgB.setOnClickListener(optionBClickListener);

            View.OnClickListener optionCClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionC,1,pos);
                }
            };
            optionC.setOnClickListener(optionCClickListener);
            opC.setOnClickListener(optionCClickListener);
            imgC.setOnClickListener(optionCClickListener);

            View.OnClickListener optionDClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionD,1,pos);
                }
            };
            optionD.setOnClickListener(optionDClickListener);
            opD.setOnClickListener(optionDClickListener);
            imgD.setOnClickListener(optionDClickListener);

            // Set options and images
            setOptionAndImage(ques, imgQues, questionsList.get(pos).getQuestion(), questionsList.get(pos).getImgQues());
            setOptionAndImage(opA, imgA, questionsList.get(pos).getOptionA(), questionsList.get(pos).getImgA());
            setOptionAndImage(opB, imgB, questionsList.get(pos).getOptionB(), questionsList.get(pos).getImgB());
            setOptionAndImage(opC, imgC, questionsList.get(pos).getOptionC(), questionsList.get(pos).getImgC());
            setOptionAndImage(opD, imgD, questionsList.get(pos).getOptionD(), questionsList.get(pos).getImgD());


        }
        private void  selectOption(LinearLayout btn,int option_num,int quesID){
            if(btnPrevSelected==null){
                btn.setBackgroundResource(R.drawable.selected_btn);
                g_quesList.get(quesID).setSelectedAns(option_num);

                changeStatus(quesID,ANSWERED);
                btnPrevSelected=btn;
            }else {
                if(btnPrevSelected.getId()==btn.getId()){
                    btn.setBackgroundResource(R.drawable.unselected_btn);
                    g_quesList.get(quesID).setSelectedAns(-1);
                    changeStatus(quesID,UNANSWERED);
                    btnPrevSelected=null;
                }
                else {
                    btnPrevSelected.setBackgroundResource(R.drawable.unselected_btn);
                    btn.setBackgroundResource(R.drawable.selected_btn);
                    g_quesList.get(quesID).setSelectedAns(option_num);
                    changeStatus(quesID,ANSWERED);
                    btnPrevSelected=btn;
                }
            }
        }

        private void changeStatus(int id, int status) {
            if (g_quesList.get(id).getStatus() != REVIEW) {
                g_quesList.get(id).setStatus(status);
            }
        }

        private void setOptionAndImage(TextView optionView, ImageView imageView, String optionText, String imageUrl) {
            if (optionText != null && !optionText.isEmpty()) {
                // If option text is not empty, show the text
                optionView.setText(optionText);
                optionView.setVisibility(View.VISIBLE);
            } else {
                // If option text is empty, hide the text
                optionView.setVisibility(View.GONE);
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // If image URL is not empty, show the image
                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imageView);
                    }
                });
                imageView.setVisibility(View.VISIBLE);
            } else {
                // If image URL is empty, hide the image
                imageView.setVisibility(View.GONE);
            }
        }


        private void setOptions(LinearLayout btn, int option_num, int quesID) {

            if (g_quesList.get(quesID).getSelectedAns() == option_num) {
                btn.setBackgroundResource(R.drawable.selected_btn);
            } else {
                btn.setBackgroundResource(R.drawable.unselected_btn);
            }
        }
    }
}

  /*          // IMAGE QUESTION
            if (imageUrlQues != null && !imageUrlQues.isEmpty()) {
                StorageReference imageRefQues = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlQues);
                imageRefQues.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imgQues);
                    }
                });
                imgQues.setVisibility(View.VISIBLE);
            } else {
                imgQues.setVisibility(View.GONE);
            }
            // IMAGE OPTION A
            if (imageUrlA != null && !imageUrlA.isEmpty()) {
                StorageReference imageRefQues = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlA);
                imageRefQues.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imgA);
                    }
                });
                imgA.setVisibility(View.VISIBLE);
            } else {
                imgA.setVisibility(View.GONE);
            }
            // IMAGE OPTION B
            if (imageUrlB != null && !imageUrlB.isEmpty()) {
                StorageReference imageRefQues = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlB);
                imageRefQues.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imgB);
                    }
                });
                imgB.setVisibility(View.VISIBLE);
            } else {
                imgB.setVisibility(View.GONE);
            }
            // IMAGE OPTION C
            if (imageUrlC!= null && !imageUrlC.isEmpty()) {
                StorageReference imageRefQues = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlC);
                imageRefQues.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imgC);
                    }
                });
                imgC.setVisibility(View.VISIBLE);
            } else {
                imgC.setVisibility(View.GONE);
            }
            // IMAGE OPTION D
            if (imageUrlD!= null && !imageUrlD.isEmpty()) {
                StorageReference imageRefQues = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlD);
                imageRefQues.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext()).load(uri.toString()).into(imgD);
                    }
                });
                imgD.setVisibility(View.VISIBLE);
            } else {
                imgD.setVisibility(View.GONE);
            }*/
/*    String imageUrlQues = questionsList.get(pos).getImgQues();
            String imageUrlA = questionsList.get(pos).getImgA();
            String imageUrlB = questionsList.get(pos).getImgB();
            String imageUrlC = questionsList.get(pos).getImgC();
            String imageUrlD= questionsList.get(pos).getImgD();*/
 /*    optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionA,1,pos);
                }
            });

            optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionB,2,pos);
                }
            });

            optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionC,3,pos);
                }
            });

            optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionD,4,pos);
                }
            });*/
