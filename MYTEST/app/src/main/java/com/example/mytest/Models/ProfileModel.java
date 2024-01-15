package com.example.mytest.Models;

public class ProfileModel {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private int bookmarksCount;
    private String ngayTao;

    public ProfileModel(String name, String email, String phone, int bookmarksCount) {
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.bookmarksCount=bookmarksCount;
    }

    public ProfileModel(String userId, String name, String email, String phone, String ngayTao) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.ngayTao = ngayTao;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBookmarksCount() {
        return bookmarksCount;
    }

    public void setBookmarksCount(int bookmarksCount) {
        this.bookmarksCount = bookmarksCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}
