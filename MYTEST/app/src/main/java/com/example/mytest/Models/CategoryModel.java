package com.example.mytest.Models;

public class CategoryModel {
    private String docID;
    private String name;
    private int noOfTests;
    private String image;

    public CategoryModel(String docID, String name, int noOfTests, String image) {
        this.docID = docID;
        this.name = name;
        this.noOfTests = noOfTests;
        this.image = image;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOfTests() {
        return noOfTests;
    }

    public void setNoOfTests(int noOfTests) {
        this.noOfTests = noOfTests;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
