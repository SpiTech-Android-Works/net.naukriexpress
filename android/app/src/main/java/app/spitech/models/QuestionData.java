package app.spitech.models;

import java.io.Serializable;

public class QuestionData implements Serializable {

    public int queId, masterQueId, quesNo;
    public String question, optionA, optionB, optionC, optionD, optionE, correctOption, solution, subjectId, shortQuestion, tempAns = "";
    public String color, positiveMarks, negativeMarks;

    public String getTempAns() {
        return this.tempAns;
    }

    public void setTempAns(String tempAns) {
        this.tempAns = tempAns;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQueId() {
        return this.queId;
    }

    public void setQueId(int queId) {
        this.queId = queId;
    }

    public int getMasterQueId() {
        return this.masterQueId;
    }

    public void setMasterQueId(int masterQueId) {
        this.masterQueId = masterQueId;
    }

    public int getQuesNo() {
        return this.quesNo;
    }

    public void setQuesNo(int quesNo) {
        this.quesNo = quesNo;
    }

    public String getShortQuestion() {
        return this.shortQuestion;
    }

    public void setShortQuestion(String shortQuestion) {
        this.shortQuestion = shortQuestion;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return this.optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return this.optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return this.optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return this.optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionE() {
        return this.optionE;
    }

    public void setOptionE(String optionE) {
        this.optionE = optionE;
    }

    public String getCorrectOption() {
        return this.correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getSolution() {
        return this.solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getPositiveMarks() {
        return this.positiveMarks;
    }

    public void setPositiveMarks(String positiveMarks) {
        this.positiveMarks = positiveMarks;
    }

    public String getNegativeMarks() {
        return this.negativeMarks;
    }

    public void setNegativeMarks(String negativeMarks) {
        this.negativeMarks = negativeMarks;
    }


}