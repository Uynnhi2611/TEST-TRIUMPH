package com.example.mytest.Models;

public class QuestionModel {

    private String qID;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String imgQues;
    private String imgA;
    private String imgB;
    private String imgC;
    private String imgD;
    private int correctAns;
    private int selectedAns;
    private int status;
    private boolean isBookmarked;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public QuestionModel(String qID, String question, String optionA, String optionB, String optionC, String optionD, String imgQues, String imgA, String imgB, String imgC, String imgD, int correctAns, int selectedAns, int status, boolean isBookmarked) {
        this.qID = qID;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.imgQues = imgQues;
        this.imgA = imgA;
        this.imgB = imgB;
        this.imgC = imgC;
        this.imgD = imgD;
        this.correctAns = correctAns;
        this.selectedAns = selectedAns;
        this.status = status;
        this.isBookmarked = isBookmarked;
    }

    public String getqID() {
        return qID;
    }

    public void setqID(String qID) {
        this.qID = qID;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public int getSelectedAns() {
        return selectedAns;
    }

    public void setSelectedAns(int selectedAns) {
        this.selectedAns = selectedAns;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public String getImgQues() {
        return imgQues;
    }

    public void setImgQues(String imgQues) {
        this.imgQues = imgQues;
    }

    public String getImgA() {
        return imgA;
    }

    public void setImgA(String imgA) {
        this.imgA = imgA;
    }

    public String getImgB() {
        return imgB;
    }

    public void setImgB(String imgB) {
        this.imgB = imgB;
    }

    public String getImgC() {
        return imgC;
    }

    public void setImgC(String imgC) {
        this.imgC = imgC;
    }

    public String getImgD() {
        return imgD;
    }

    public void setImgD(String imgD) {
        this.imgD = imgD;
    }
}
/*    public QuestionModel(String qID, String question, String optionA, String optionB, String optionC, String optionD, int correctAns, int selectedAns, int status, boolean isBookmarked) {
        this.qID= qID;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns = correctAns;
        this.selectedAns=selectedAns;
        this.status=status;
        this.isBookmarked=isBookmarked;
    }*/