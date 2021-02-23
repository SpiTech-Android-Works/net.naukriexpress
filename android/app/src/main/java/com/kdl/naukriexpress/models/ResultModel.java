package com.kdl.naukriexpress.models;

import java.io.Serializable;

/**
 * Created by in.gdc4gpatnew.spitech on 12/24/17.
 */

public class ResultModel implements Serializable {

    public String testName = "", startTime = "", endTime = "0", testCategory = "", date = "", rank = "", marks = "", duration = "";
    public int id = 0, testId = 0;
    public String color;

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRowId() {
        return this.id;
    }

    public void setRowId(int id) {
        this.id = id;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getTestId() {
        return this.testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTestCategory() {
        return this.testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMarks() {
        return this.marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

}
