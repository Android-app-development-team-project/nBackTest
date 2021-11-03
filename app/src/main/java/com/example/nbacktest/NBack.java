package com.example.nbacktest;

import android.util.Log;

import java.util.Random;

public class NBack {

    private int n;
    private int examLength;
    private int delayTime;
    private boolean parallel;
    private String exam;
    private String rightAnswer;
    private String userAnswer;
    private int score;
    private Random random;

    // 생성자 매개변수들: N, 문제길이, delayTime, 병렬여부
    public NBack(int n, int examLength, int delayTime, boolean parallel){
        this.n = n;
        this.examLength = examLength;
        this.delayTime = delayTime * 1000;
        this.parallel = parallel;
        random = new Random();
    }

    // 난수로 문제 생성   => 가능한 문제의 30퍼센트 이상은 "O" 나오도록 만들기!
    public void createExam(){
        for(int i=0; i < examLength; i++){
            exam += String.valueOf(random.nextInt(10));
            if(i < n)                                               // n-back이므로 답지의 크기는 examLength - n
                continue;
            // 답지 생성
            if(exam.charAt(i) == exam.charAt(i-n))
                rightAnswer += "O";
            else
                rightAnswer += "X";
        }
    }

    public void init(){
        exam = "";
        rightAnswer = "";
        score = 0;

        createExam();
    }

    // 스코어 계산()
    public void caculateScore(){
        for(int i=0; i<rightAnswer.length(); i++){
            if (rightAnswer.charAt(i) == 'O' && userAnswer.charAt(i) == 'O')
                score++;
        }
    }
    
    // 각 변수들 get, set 메소드
    public int getN() { return n; }
    public void setN(int n) { this.n = n; }

    public int getExamLength() { return examLength; }
    public void setExamLength(int examLength) { this.examLength = examLength; }

    public int getDelayTime() { return delayTime; }
    public void setDelayTime(int delayTime) { this.delayTime = delayTime; }

    public boolean isParallel() { return parallel; }
    public void setParallel(boolean parallel) { this.parallel = parallel; }

    public String getExam() { return exam; }
    public void setExam(String exam) { this.exam = exam; }

    public String getRightAnswer() { return rightAnswer; }
    public void setRightAnswer(String rightAnswer) { this.rightAnswer = rightAnswer; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
