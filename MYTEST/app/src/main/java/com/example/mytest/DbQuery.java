package com.example.mytest;

import android.util.ArrayMap;

import androidx.annotation.NonNull;


import com.example.mytest.Models.CategoryModel;
import com.example.mytest.Models.ProfileModel;
import com.example.mytest.Models.QuestionModel;
import com.example.mytest.Models.RankModel;
import com.example.mytest.Models.TestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DbQuery {
    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();
    public static int g_selected_test_index = 0;
    public static int g_selected_ques_index = 0;
    public static List<String> g_bmIdList = new ArrayList<>();
    public static List<QuestionModel> g_bookmarksList = new ArrayList<>();
    public static List<QuestionModel> g_quesList = new ArrayList<>();
    public static List<RankModel> g_usersList = new ArrayList<>();
    public static List<ProfileModel> g_usersData = new ArrayList<>();
    public static int g_usersCount = 0;
    public static boolean isMeOnTopList = false;
    public static ProfileModel myProfile = new ProfileModel("NA", null, null, 0);
    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;
    public static RankModel myPerformance = new RankModel("NULL", 0, -1);
    static int tmp;
    public static final int PICK_IMAGE_REQUEST_Ques = 1;
    public static final int PICK_IMAGE_REQUEST_A = 2;
    public static final int PICK_IMAGE_REQUEST_B = 3;
    public static final int PICK_IMAGE_REQUEST_C = 4;
    public static final int PICK_IMAGE_REQUEST_D = 5;

    public static void createUserData(String email, String name, MyCompleteListener completeListener) {
        Map<String, Object> userData = new ArrayMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
// Lưu trữ thông tin người dùng trong Firestore

        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("TOTAL_SCORE", 0);
        userData.put("ngayTao", currentDate);
        userData.put("PHONE", "");


        DocumentReference userDoc = g_firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        WriteBatch batch = g_firestore.batch();
        batch.set(userDoc, userData);

        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc, "COUNT", FieldValue.increment(1));
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void saveProfileData(String name, String phone, MyCompleteListener completeListener) {

        Map<String, Object> profileData = new ArrayMap<>();
        profileData.put("NAME", name);

        if (phone != null)
            profileData.put("PHONE", phone);
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .update(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        myProfile.setName(name);
                        if (phone != null)
                            myProfile.setPhone(phone);

                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void getUserData(MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myProfile.setName(documentSnapshot.getString("NAME"));
                        myProfile.setEmail(documentSnapshot.getString("EMAIL_ID"));
                        if (documentSnapshot.getString("PHONE") != null)
                            myProfile.setPhone(documentSnapshot.getString("PHONE"));
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void loadUserData(MyCompleteListener completeListener) {
        g_usersData.clear();
        g_firestore.collection("USERS").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot userDoc : queryDocumentSnapshots) {
                            // Kiểm tra xem tài liệu có phải là "TOTAL_USERS" không
                            if (!userDoc.getId().equals("TOTAL_USERS")) {
                                String userId = userDoc.getId(); // Lấy ID của người dùng
                                String emailId = userDoc.getString("EMAIL_ID");
                                String name = userDoc.getString("NAME");
                                String phone = userDoc.getString("PHONE");
                                String ngayTao = userDoc.getString("ngayTao");

                                g_usersData.add(new ProfileModel(userId, name, emailId, phone, ngayTao));
                            }
                        }
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void deleteUser(String userId, MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(userId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Giảm COUNT trong TOTAL_USERS
                        g_firestore.collection("USERS").document("TOTAL_USERS")
                                .update("COUNT", FieldValue.increment(-1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        completeListener.onSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        completeListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void loadMyScores(MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        for (int i = 0; i < g_testList.size(); i++) {
                            int top = 0;
                            if (documentSnapshot.get(g_testList.get(i).getTestID()) != null) {
                                top = documentSnapshot.getLong(g_testList.get(i).getTestID()).intValue();
                            }
                            g_testList.get(i).setTopScore(top);
                        }
                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();

                    }
                });
    }
    public static void loadBmIds(MyCompleteListener completeListener) {
        g_bmIdList.clear();
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int count = myProfile.getBookmarksCount();

                        for (int i = 0; i < count; i++) {
                            String bmID = documentSnapshot.getString("BM" + String.valueOf(i + 1) + "_ID");
                            g_bmIdList.add(bmID);
                        }
                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });

    }
    public static void loadBookmarks(MyCompleteListener completeListener) {
        g_bookmarksList.clear();
        tmp = 0;

        if (g_bmIdList.size() == 0) {
            completeListener.onSuccess();
        }

        for (int i = 0; i < g_bmIdList.size(); i++) {
            String docID = g_bmIdList.get(i);

            g_firestore.collection("Questions").document(docID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                g_bookmarksList.add(new QuestionModel(
                                        documentSnapshot.getId(),
                                        documentSnapshot.getString("QUESTION"),
                                        documentSnapshot.getString("A"),
                                        documentSnapshot.getString("B"),
                                        documentSnapshot.getString("C"),
                                        documentSnapshot.getString("D"),
                                        documentSnapshot.getString("IMGQUES"),
                                        documentSnapshot.getString("IMGA"),
                                        documentSnapshot.getString("IMGB"),
                                        documentSnapshot.getString("IMGC"),
                                        documentSnapshot.getString("IMGD"),
                                        documentSnapshot.getLong("ANSWER").intValue(),
                                        0,
                                        -1,
                                        false
                                ));
                            }
                            tmp++;
                            if (tmp == g_bmIdList.size()) {
                                completeListener.onSuccess();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            completeListener.onFailure();
                        }
                    });
        }
    }
    public static void getTopUsers(MyCompleteListener completeListener) {
        g_usersList.clear();
        String myUID = FirebaseAuth.getInstance().getUid();
        g_firestore.collection("USERS")
                .whereGreaterThan("TOTAL_SCORE", 0)
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int rank = 1;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            g_usersList.add(new RankModel(
                                    doc.getString("NAME"),
                                    doc.getLong("TOTAL_SCORE").intValue(),
                                    rank
                            ));

                            if (myUID.compareTo(doc.getId()) == 0) {
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }
                            rank++;
                        }

                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void saveResult(int score, MyCompleteListener completeListener) {
        WriteBatch batch = g_firestore.batch();
        //Bookmarks
        Map<String, Object> bmData = new ArrayMap<>();
        for (int i = 0; i < g_bmIdList.size(); i++) {
            bmData.put("BM" + String.valueOf(i + 1) + "_ID", g_bmIdList.get(i));
        }
        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");

        batch.set(bmDoc, bmData);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("TOTAL_SCORE", score);
        userData.put("BOOKMARKS", g_bmIdList.size());

        batch.update(userDoc, userData);


        if (score > g_testList.get(g_selected_test_index).getTopScore()) {
            DocumentReference scoreDoc = userDoc.collection("USER_DATA").document("MY_SCORES");
            Map<String, Object> testData = new ArrayMap<>();
            testData.put(g_testList.get(g_selected_test_index).getTestID(), score);
            batch.set(scoreDoc, testData, SetOptions.merge());

        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (score > g_testList.get(g_selected_test_index).getTopScore())
                            g_testList.get(g_selected_test_index).setTopScore(score);

                        myPerformance.setScore(score);
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });

    }
    public static void loadCategories(MyCompleteListener completeListener) {
        g_catList.clear();
        g_firestore.collection("QUIZ").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            docList.put(doc.getId(), doc);
                        }
                        QueryDocumentSnapshot catListDooc = docList.get("Categories");

                        long catCount = catListDooc.getLong("COUNT");

                        for (int i = 1; i <= catCount; i++) {
                            String catID = catListDooc.getString("CAT" + String.valueOf(i) + "_ID");
                            QueryDocumentSnapshot catDoc = docList.get(catID);
                            String imageUrl = catDoc.getString("IMAGE");
                            String catName = catDoc.getString("NAME");
                            int noOfTests = catDoc.getLong("NO_OF_TESTS").intValue(); // Get the number of tests

                            g_catList.add(new CategoryModel(catID, catName,noOfTests, imageUrl)); // Add the number of tests to the model
                        }
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void loadQuestions(MyCompleteListener completeListener) {
        g_quesList.clear();
        g_firestore.collection("Questions")
                .whereEqualTo("CATEGORY", g_catList.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testList.get(g_selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            boolean isBookmarked = false;
                            if (g_bmIdList.contains(doc.getId()))
                                isBookmarked = true;
                            g_quesList.add(new QuestionModel(
                                    doc.getId(),
                                    doc.getString("QUESTION"),
                                    doc.getString("A"),
                                    doc.getString("B"),
                                    doc.getString("C"),
                                    doc.getString("D"),
                                    doc.getString("IMGQUES"),
                                    doc.getString("IMGA"),
                                    doc.getString("IMGB"),
                                    doc.getString("IMGC"),
                                    doc.getString("IMGD"),
                                    doc.getLong("ANSWER").intValue(),
                                    -1,
                                    NOT_VISITED,
                                    isBookmarked
                            ));
                        }
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void addQuestion(QuestionModel newQuestion, MyCompleteListener completeListener) {
        // Tạo một Map để lưu trữ dữ liệu của câu hỏi mới
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("QUESTION", newQuestion.getQuestion());
        questionData.put("A", newQuestion.getOptionA());
        questionData.put("B", newQuestion.getOptionB());
        questionData.put("C", newQuestion.getOptionC());
        questionData.put("D", newQuestion.getOptionD());
        questionData.put("IMGQUES", newQuestion.getImgQues());
        questionData.put("IMGA", newQuestion.getImgA());
        questionData.put("IMGB", newQuestion.getImgB());
        questionData.put("IMGC", newQuestion.getImgC());
        questionData.put("IMGD", newQuestion.getImgD());
        questionData.put("ANSWER", newQuestion.getCorrectAns());
        questionData.put("CATEGORY", g_catList.get(g_selected_cat_index).getDocID());
        questionData.put("TEST", g_testList.get(g_selected_test_index).getTestID());
        

        // Thêm câu hỏi mới vào Firestore
        g_firestore.collection("Questions")
                .add(questionData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Cập nhật ID của câu hỏi mới
                        newQuestion.setqID(documentReference.getId());

                        // Thêm câu hỏi mới vào danh sách câu hỏi
                        g_quesList.add(newQuestion);

                        // Gọi phương thức onSuccess của completeListener
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gọi phương thức onFailure của completeListener
                        completeListener.onFailure();
                    }
                });
    }

    public static void deleteQuestion(String questionId, MyCompleteListener completeListener) {
        g_firestore.collection("Questions")
                .document(questionId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa câu hỏi khỏi danh sách câu hỏi
                        for (int i = 0; i < g_quesList.size(); i++) {
                            if (g_quesList.get(i).getqID().equals(questionId)) {
                                g_quesList.remove(i);
                                break;
                            }
                        }
                        // Gọi phương thức onSuccess của completeListener
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gọi phương thức onFailure của completeListener
                        completeListener.onFailure();
                    }
                });
    }

    public static void loadTestData(MyCompleteListener completeListener) {
       g_testList.clear();
       String catDocID = g_catList.get(g_selected_cat_index).getDocID();
       g_firestore.collection("QUIZ").document(catDocID)
               .collection("TESTS_LIST")
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                           String testID = documentSnapshot.getString("TEST_ID");
                           String docID = documentSnapshot.getId(); // Get the auto-generated document ID
                           g_testList.add(new TestModel(
                                   testID,
                                   documentSnapshot.getString("TEST_NAME"),
                                   0,
                                   documentSnapshot.getLong("TEST_TIME").intValue(),
                                   "/QUIZ/" + catDocID + "/TESTS_LIST/" + docID //+ "/" + testID // updated path
                           ));
                       }

                       completeListener.onSuccess();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       completeListener.onFailure();
                   }
               });
   }
    public static void getUsersCount(MyCompleteListener completeListener){
        g_firestore.collection("USERS").document("TOTAL_USERS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        g_usersCount=documentSnapshot.getLong("COUNT").intValue();

                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });

    }
    public static void loadData(MyCompleteListener completeListener) {
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        getUsersCount(new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                loadBmIds(new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        for (CategoryModel category : g_catList) {
                                            g_selected_cat_index = g_catList.indexOf(category);
                                            loadTestData(new MyCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    if (g_catList.indexOf(category) == g_catList.size() - 1) {
                                                        completeListener.onSuccess();
                                                    }
                                                }
                                                @Override
                                                public void onFailure() {
                                                    completeListener.onFailure();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure() {
                                        completeListener.onFailure();
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                completeListener.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        completeListener.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                completeListener.onFailure();
            }
        });
    }
    public static List<TestModel> findTest(String testId, String testName) {
        return g_testList.stream()
                .filter(test -> test.getTestID().equals(testId) || test.getTestName().equals(testName))
                .collect(Collectors.toList());
    }
}
/* public static void loadTestData(MyCompleteListener completeListener) {
        g_testList.clear();
        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocID())
                .collection("TESTS_LIST").document("TESTS_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int noOfTests = g_catList.get(g_selected_cat_index).getNoOfTests();
                        for (int i = 1; i <= noOfTests; i++) {
                            g_testList.add(new TestModel(
                                    documentSnapshot.getString("TEST" + String.valueOf(i) + "_ID"), 0,
                                    documentSnapshot.getLong("TEST" + String.valueOf(i) + "_TIME").intValue()
                            ));
                        }

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });

    }*/
/* public static void createCategory(CategoryModel newCategory, MyCompleteListener completeListener) {
        // Lấy ID của người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        // Tạo một Map để lưu trữ dữ liệu của danh mục mới
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("NAME", newCategory.getName());
        categoryData.put("NO_OF_TESTS", newCategory.getNoOfTests());
        // Tạo một document mới với ID ngẫu nhiên trong subcollection USERS/userID/QUIZ
        DocumentReference newDoc = g_firestore.collection("USERS").document(userID).collection("QUIZ").document();
        String newDocID = newDoc.getId();
        // Sử dụng ID của document mới làm CAT_ID
        categoryData.put("CAT_ID", newDocID);
        // Thêm danh mục mới vào Firestore
        newDoc.set(categoryData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật ID của danh mục mới
                        newCategory.setDocID(newDoc.getId());
                        // Cập nhật số lượng category trong mục "Categories"
                        DocumentReference categoriesDoc = g_firestore.collection("USERS").document(userID).collection("QUIZ").document("Categories");
                        categoriesDoc.update("COUNT", FieldValue.increment(1));
                        // Lấy số lượng category hiện tại
                        categoriesDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Long count = documentSnapshot.getLong("COUNT");
                                // Cập nhật CAT[i]_ID trong "Categories"
                                categoriesDoc.update("CAT" + count + "_ID", newDoc.getId());
                            }
                        });
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
        ////////////
        // Tạo một document với ID giống như document mới trong collection QUIZ
        DocumentReference newDoc2 = g_firestore.collection("QUIZ").document(newDocID);
        // Thêm danh mục mới vào Firestore
        newDoc2.set(categoryData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật số lượng category trong mục "Categories"
                        DocumentReference categoriesDoc2 = g_firestore.collection("QUIZ").document("Categories");
                        categoriesDoc2.update("COUNT", FieldValue.increment(1));
                        // Lấy số lượng category hiện tại
                        categoriesDoc2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Long count = documentSnapshot.getLong("COUNT");
                                // Cập nhật CAT[i]_ID trong "Categories"
                                categoriesDoc2.update("CAT" + count + "_ID", newDocID);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
    public static void deleteCategory(String catId, MyCompleteListener completeListener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        // Lấy tham chiếu đến document chứa thông tin danh mục trong QUIZ
        DocumentReference catDocRef = g_firestore.collection("QUIZ").document(catId);

        // Lấy tham chiếu đến document chứa thông tin danh mục trong USERS\id\QUIZ
        DocumentReference userCatDocRef = g_firestore.collection("USERS").document(userID).collection("QUIZ").document(catId);

        // Xóa document từ Firestore
        catDocRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa document từ USERS\id\QUIZ
                        userCatDocRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Giảm số lượng danh mục trong "Categories"
                                        DocumentReference categoriesDoc = g_firestore.collection("QUIZ").document("Categories");
                                        categoriesDoc.update("COUNT", FieldValue.increment(-1));

                                        // Lấy số lượng danh mục hiện tại và cập nhật lại chỉ số
                                        categoriesDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Long count = documentSnapshot.getLong("COUNT");
                                                WriteBatch batch = g_firestore.batch();

                                                for (int i = 1; i <= count; i++) {
                                                    String nextCatId = documentSnapshot.getString("CAT" + (i + 1) + "_ID");
                                                    if (nextCatId != null) {
                                                        batch.update(categoriesDoc, "CAT" + i + "_ID", nextCatId);
                                                        batch.update(categoriesDoc, "CAT" + (i + 1) + "_ID", FieldValue.delete());
                                                    }
                                                }

                                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Xóa danh mục khỏi g_catList
                                                            for (int i = 0; i < g_catList.size(); i++) {
                                                                if (g_catList.get(i).getDocID().equals(catId)) {
                                                                    g_catList.remove(i);
                                                                    break;
                                                                }
                                                            }
                                                            completeListener.onSuccess();
                                                        } else {
                                                            completeListener.onFailure();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        completeListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }*/
/*  public static void loadData(MyCompleteListener completeListener) {
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {

                        getUsersCount(new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                loadBmIds(completeListener);
                            }

                            @Override
                            public void onFailure() {
                                completeListener.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        completeListener.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                completeListener.onFailure();
            }
        });
    }
*/
/*  public static void loadFoundTest(String testPath, MyCompleteListener completeListener) {
        g_testList.clear();

        // Split the testPath and get the necessary IDs
        String[] pathParts = testPath.split("/");
        String catDocID = pathParts[2];
        String docID = pathParts[4];

        g_firestore.collection("QUIZ").document(catDocID)
                .collection("TESTS_LIST").document(docID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        g_foundTestList.add(new TestModel(
                                docID,
                                documentSnapshot.getString("TEST_NAME"),
                                0,
                                documentSnapshot.getLong("TEST_TIME").intValue(),
                                testPath
                        ));

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }*/