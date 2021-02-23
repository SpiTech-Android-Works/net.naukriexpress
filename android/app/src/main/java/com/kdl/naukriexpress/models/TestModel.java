package com.kdl.naukriexpress.models;

import java.io.Serializable;

/**
 * Created by in.gdc4gpatnew.spitech on 12/22/17.
 */

public class TestModel implements Serializable {

    public String rowId, publishDate;
    public String name, totalMarks, totalQuestions, duration, viewCounter, rank, correct, attempted;
    public int isAttempted = 0;

    public int getIsAttempted() {
        return this.isAttempted;
    }

    public void setIsAttempted(int isAttempted) {
        this.isAttempted = isAttempted;
    }

    public String getRowId() {
        return this.rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getCorrect() {
        return this.correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getAttempted() {
        return this.attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String totalMarks() {
        return this.totalMarks;
    }

    public String getTotalQuestions() {
        return this.totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getViewCounter() {
        return this.viewCounter;
    }

    public void setViewCounter(String viewCounter) {
        this.viewCounter = viewCounter;
    }


}
