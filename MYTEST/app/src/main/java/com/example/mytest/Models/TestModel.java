package com.example.mytest.Models;

public class TestModel {
    private String testID;
    private String testName;
    private int topScore;
    private int time;
    private String testPath;
  /*  public TestModel(String testID, String testName, int topScore, int time) {
        this.testID = testID;
        this.testName = testName;
        this.topScore = topScore;
        this.time = time;
    }*/

    public TestModel(String testID, String testName, int topScore, int time, String testPath) {
        this.testID = testID;
        this.testName = testName;
        this.topScore = topScore;
        this.time = time;
        this.testPath = testPath;
    }

    public String getTestPath() {
        return testPath;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public int getTopScore() {
        return topScore;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
